#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiUdp.h>

// =========================
// Access Point Configuration
// =========================

namespace Config {
  constexpr char AP_SSID[]     = "ESP8266-IoT";
  constexpr char AP_PASSWORD[] = "esp12345678";   // 8+ chars, or "" for open network

  const IPAddress LOCAL_IP(192, 168, 4, 1);
  const IPAddress GATEWAY(192, 168, 4, 1);
  const IPAddress SUBNET(255, 255, 255, 0);

  constexpr char DEVICE_NAME[]  = "ESP8266-01";
  constexpr uint16_t HTTP_PORT  = 80;
  constexpr uint16_t UDP_PORT   = 4210;
}

// =========================
// Relay/Channel Definition
// =========================

struct Relay {
  const char* name;
  uint8_t pin;
  bool activeLow;
  bool state;
};

Relay relays[] = {
  { "relay1", D4, true, false },
  { "relay2", D5, true, false }
};

constexpr size_t RELAY_COUNT = sizeof(relays) / sizeof(relays[0]);

ESP8266WebServer server(Config::HTTP_PORT);
WiFiUDP udp;
char udpBuffer[128];

// =========================
// Relay Control
// =========================

void applyRelayState(Relay& relay) {
  digitalWrite(relay.pin, relay.state ^ relay.activeLow ? HIGH : LOW);
}

void setRelay(Relay& relay, bool state) {
  relay.state = state;
  applyRelayState(relay);

  Serial.printf("[Relay] %s -> %s\n", relay.name, state ? "ON" : "OFF");
}

int findRelayIndex(const String& name) {
  for (size_t i = 0; i < RELAY_COUNT; i++) {
    if (name.equalsIgnoreCase(relays[i].name)) {
      return static_cast<int>(i);
    }
  }
  return -1;
}

// =========================
// JSON Helpers
// =========================

String buildStatusJson() {
  String json = "{";
  json += "\"device\":\"" + String(Config::DEVICE_NAME) + "\",";
  json += "\"ip\":\"" + WiFi.softAPIP().toString() + "\",";
  json += "\"chipId\":\"" + String(ESP.getChipId(), HEX) + "\",";
  json += "\"relays\":[";

  for (size_t i = 0; i < RELAY_COUNT; i++) {
    json += "{\"name\":\"" + String(relays[i].name) + "\",";
    json += "\"status\":\"" + String(relays[i].state ? "ON" : "OFF") + "\"}";
    if (i < RELAY_COUNT - 1) json += ",";
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

void handleRelaySet(bool turnOn) {
  if (!server.hasArg("relay")) {
    sendJson(400, "{\"error\":\"Missing 'relay' parameter\"}");
    return;
  }

  int idx = findRelayIndex(server.arg("relay"));
  if (idx < 0) {
    sendJson(404, "{\"error\":\"Unknown relay\"}");
    return;
  }

  setRelay(relays[idx], turnOn);
  sendJson(200, buildStatusJson());
}

void handleRelayOn()  { handleRelaySet(true); }
void handleRelayOff() { handleRelaySet(false); }

void handleNotFound() {
  sendJson(404, "{\"error\":\"Not found\"}");
}

// =========================
// UDP Discovery
// =========================

void sendDiscoveryResponse(IPAddress remoteIP, uint16_t remotePort) {
  String response = buildStatusJson();

  udp.beginPacket(remoteIP, remotePort);
  udp.write(response.c_str());
  udp.endPacket();

  Serial.print("[UDP] Discovery response sent to: ");
  Serial.println(remoteIP);
}

void handleUdpDiscovery() {
  int packetSize = udp.parsePacket();
  if (packetSize <= 0) return;

  int length = udp.read(udpBuffer, sizeof(udpBuffer) - 1);
  if (length <= 0) return;

  udpBuffer[length] = '\0';

  Serial.printf("[UDP] Packet from %s:%d -> %s\n",
                udp.remoteIP().toString().c_str(),
                udp.remotePort(),
                udpBuffer);

  if (strcmp(udpBuffer, "DISCOVER_ESP") == 0) {
    sendDiscoveryResponse(udp.remoteIP(), udp.remotePort());
  }
}

// =========================
// Access Point Setup
// =========================

void startAccessPoint() {
  Serial.println();
  Serial.println("[WiFi] Starting Access Point...");

  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(Config::LOCAL_IP, Config::GATEWAY, Config::SUBNET);
  WiFi.softAP(Config::AP_SSID, Config::AP_PASSWORD);

  Serial.print("[WiFi] SSID: ");
  Serial.println(Config::AP_SSID);
  Serial.print("[WiFi] AP IP address: ");
  Serial.println(WiFi.softAPIP());
  Serial.print("[WiFi] Chip ID: ");
  Serial.println(String(ESP.getChipId(), HEX));
}

// =========================
// Setup
// =========================

void setup() {
  Serial.begin(9600);
  delay(100);

  for (size_t i = 0; i < RELAY_COUNT; i++) {
    pinMode(relays[i].pin, OUTPUT);
    setRelay(relays[i], false);
  }

  startAccessPoint();

  if (udp.begin(Config::UDP_PORT)) {
    Serial.print("[UDP] Discovery service started on port: ");
    Serial.println(Config::UDP_PORT);
  } else {
    Serial.println("[UDP] Initialization failed!");
  }

  server.on("/status", HTTP_GET, handleStatus);
  server.on("/relay/on", HTTP_GET, handleRelayOn);
  server.on("/relay/off", HTTP_GET, handleRelayOff);
  server.onNotFound(handleNotFound);

  server.begin();

  Serial.print("[HTTP] Server started on port: ");
  Serial.println(Config::HTTP_PORT);
}

// =========================
// Loop
// =========================

void loop() {
  server.handleClient();
  handleUdpDiscovery();
}