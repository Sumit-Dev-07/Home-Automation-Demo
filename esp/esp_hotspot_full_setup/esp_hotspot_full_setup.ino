#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiUdp.h>
#include <EEPROM.h>

// =========================
// Configuration
// =========================

namespace Config {
  char AP_SSID[32]     = "ESP-Home-Sync";
  char AP_PASSWORD[64] = "esp12345678";

  const IPAddress LOCAL_IP(192, 168, 4, 1);
  const IPAddress GATEWAY(192, 168, 4, 1);
  const IPAddress SUBNET(255, 255, 255, 0);

  constexpr char DEVICE_NAME[]  = "ESP-Dynamic-Hub";
  constexpr uint16_t HTTP_PORT  = 80;
  constexpr uint16_t UDP_PORT   = 4210;

  constexpr int EEPROM_SIZE = 512;
  constexpr int MAGIC_NUMBER = 0xABCD;
}

// =========================
// Device Structure
// =========================

struct Device {
  char name[20];
  uint8_t relayPin;
  uint8_t switchPin; // 255 if no sync switch
  bool state;
  bool lastSwitchState;
  bool active;
};

Device devices[8];
constexpr size_t MAX_DEVICES = 8;

ESP8266WebServer server(Config::HTTP_PORT);
WiFiUDP udp;
char udpBuffer[128];

// =========================
// Pin Mapping (NodeMCU)
// =========================

uint8_t getPin(String pinName) {
  if (pinName == "D0") return 16;
  if (pinName == "D1") return 5;
  if (pinName == "D2") return 4;
  if (pinName == "D3") return 0;
  if (pinName == "D4") return 2;
  if (pinName == "D5") return 14;
  if (pinName == "D6") return 12;
  if (pinName == "D7") return 13;
  if (pinName == "D8") return 15;
  return 255;
}

String getPinName(uint8_t pin) {
  switch(pin) {
    case 16: return "D0";
    case 5:  return "D1";
    case 4:  return "D2";
    case 0:  return "D3";
    case 2:  return "D4";
    case 14: return "D5";
    case 12: return "D6";
    case 13: return "D7";
    case 15: return "D8";
    default: return "None";
  }
}

// =========================
// EEPROM Management
// =========================

void saveConfig() {
  int addr = 0;
  EEPROM.put(addr, Config::MAGIC_NUMBER);
  addr += sizeof(Config::MAGIC_NUMBER);
  EEPROM.put(addr, Config::AP_PASSWORD);
  addr += sizeof(Config::AP_PASSWORD);
  EEPROM.put(addr, devices);
  EEPROM.commit();
  Serial.println("[Config] Saved to EEPROM");
}

void loadConfig() {
  int addr = 0;
  int magic;
  EEPROM.get(addr, magic);
  if (magic == Config::MAGIC_NUMBER) {
    addr += sizeof(Config::MAGIC_NUMBER);
    EEPROM.get(addr, Config::AP_PASSWORD);
    addr += sizeof(Config::AP_PASSWORD);
    EEPROM.get(addr, devices);
    Serial.println("[Config] Loaded from EEPROM");
  } else {
    Serial.println("[Config] No valid config found, using defaults");
    for (int i = 0; i < MAX_DEVICES; i++) devices[i].active = false;
    saveConfig();
  }
}

// =========================
// Relay Control
// =========================

void applyDeviceState(int i) {
  if (!devices[i].active) return;
  digitalWrite(devices[i].relayPin, devices[i].state ? LOW : HIGH); // Active Low assumed
}

void setDevice(int i, bool state) {
  if (i < 0 || i >= MAX_DEVICES || !devices[i].active) return;
  devices[i].state = state;
  applyDeviceState(i);
  Serial.printf("[Device] %s -> %s\n", devices[i].name, state ? "ON" : "OFF");
}

// =========================
// Two-Way Sync Logic
// =========================

void handlePhysicalSwitches() {
  for (int i = 0; i < MAX_DEVICES; i++) {
    if (devices[i].active && devices[i].switchPin != 255) {
      bool currentSwitchState = digitalRead(devices[i].switchPin);
      if (currentSwitchState != devices[i].lastSwitchState) {
        devices[i].lastSwitchState = currentSwitchState;
        setDevice(i, !devices[i].state);
      }
    }
  }
}

// =========================
// JSON Helpers
// =========================

String buildStatusJson() {
  String json = "{";
  json += "\"device\":\"" + String(Config::DEVICE_NAME) + "\",";
  json += "\"ip\":\"" + WiFi.softAPIP().toString() + "\",";
  json += "\"relays\":[";

  bool first = true;
  for (int i = 0; i < MAX_DEVICES; i++) {
    if (devices[i].active) {
      if (!first) json += ",";
      json += "{\"name\":\"" + String(devices[i].name) + "\",";
      json += "\"pin\":\"" + getPinName(devices[i].relayPin) + "\",";
      json += "\"syncPin\":\"" + getPinName(devices[i].switchPin) + "\",";
      json += "\"status\":\"" + String(devices[i].state ? "ON" : "OFF") + "\"}";
      first = false;
    }
  }

  json += "]}";
  return json;
}

void sendJson(int code, const String& body) {
  server.send(code, "application/json", body);
}

// =========================
// HTTP Handlers
// =========================

void handleStatus() {
  sendJson(200, buildStatusJson());
}

void handleRelayToggle() {
  if (!server.hasArg("relay")) {
    sendJson(400, "{\"error\":\"Missing 'relay' parameter\"}");
    return;
  }
  String name = server.arg("relay");
  bool turnOn = server.arg("status") == "ON";

  for (int i = 0; i < MAX_DEVICES; i++) {
    if (devices[i].active && name.equalsIgnoreCase(devices[i].name)) {
      setDevice(i, turnOn);
      sendJson(200, buildStatusJson());
      return;
    }
  }
  sendJson(404, "{\"error\":\"Unknown relay\"}");
}

void handleAddDevice() {
  if (!server.hasArg("name") || !server.hasArg("pin")) {
    sendJson(400, "{\"error\":\"Missing name or pin\"}");
    return;
  }

  int emptyIdx = -1;
  for (int i = 0; i < MAX_DEVICES; i++) {
    if (!devices[i].active) {
      emptyIdx = i;
      break;
    }
  }

  if (emptyIdx == -1) {
    sendJson(500, "{\"error\":\"Device list full\"}");
    return;
  }

  strncpy(devices[emptyIdx].name, server.arg("name").c_str(), 19);
  devices[emptyIdx].relayPin = getPin(server.arg("pin"));
  devices[emptyIdx].switchPin = server.hasArg("syncPin") ? getPin(server.arg("syncPin")) : 255;
  devices[emptyIdx].state = false;
  devices[emptyIdx].active = true;

  pinMode(devices[emptyIdx].relayPin, OUTPUT);
  applyDeviceState(emptyIdx);

  if (devices[emptyIdx].switchPin != 255) {
    pinMode(devices[emptyIdx].switchPin, INPUT_PULLUP);
    devices[emptyIdx].lastSwitchState = digitalRead(devices[emptyIdx].switchPin);
  }

  saveConfig();
  sendJson(200, buildStatusJson());
}

void handleRemoveDevice() {
  if (!server.hasArg("name")) {
    sendJson(400, "{\"error\":\"Missing name\"}");
    return;
  }
  String name = server.arg("name");
  for (int i = 0; i < MAX_DEVICES; i++) {
    if (devices[i].active && name.equalsIgnoreCase(devices[i].name)) {
      devices[i].active = false;
      saveConfig();
      sendJson(200, buildStatusJson());
      return;
    }
  }
  sendJson(404, "{\"error\":\"Not found\"}");
}

void handleUpdateWifi() {
  if (!server.hasArg("password")) {
    sendJson(400, "{\"error\":\"Missing password\"}");
    return;
  }
  strncpy(Config::AP_PASSWORD, server.arg("password").c_str(), 63);
  saveConfig();
  sendJson(200, "{\"message\":\"Password updated. Restarting...\"}");
  delay(1000);
  ESP.restart();
}

// =========================
// Discovery
// =========================

void handleUdpDiscovery() {
  int packetSize = udp.parsePacket();
  if (packetSize <= 0) return;
  int length = udp.read(udpBuffer, sizeof(udpBuffer) - 1);
  if (length <= 0) return;
  udpBuffer[length] = '\0';

  if (strcmp(udpBuffer, "DISCOVER_ESP") == 0) {
    String response = buildStatusJson();
    udp.beginPacket(udp.remoteIP(), udp.remotePort());
    udp.write(response.c_str());
    udp.endPacket();
  }
}

// =========================
// Setup
// =========================

void setup() {
  Serial.begin(115200);
  EEPROM.begin(Config::EEPROM_SIZE);
  loadConfig();

  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(Config::LOCAL_IP, Config::GATEWAY, Config::SUBNET);
  WiFi.softAP(Config::AP_SSID, Config::AP_PASSWORD);

  Serial.println("[WiFi] AP Started");
  Serial.println(WiFi.softAPIP());

  udp.begin(Config::UDP_PORT);

  for (int i = 0; i < MAX_DEVICES; i++) {
    if (devices[i].active) {
      pinMode(devices[i].relayPin, OUTPUT);
      applyDeviceState(i);
      if (devices[i].switchPin != 255) {
        pinMode(devices[i].switchPin, INPUT_PULLUP);
        devices[i].lastSwitchState = digitalRead(devices[i].switchPin);
      }
    }
  }

  server.on("/status", HTTP_GET, handleStatus);
  server.on("/relay/toggle", HTTP_GET, handleRelayToggle);
  server.on("/device/add", HTTP_GET, handleAddDevice);
  server.on("/device/remove", HTTP_GET, handleRemoveDevice);
  server.on("/wifi/update", HTTP_GET, handleUpdateWifi);

  server.begin();
}

void loop() {
  server.handleClient();
  handleUdpDiscovery();
  handlePhysicalSwitches();
}
