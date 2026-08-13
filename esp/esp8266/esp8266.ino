#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiUdp.h>

// =========================
// Wi-Fi Configuration
// =========================

const char* WIFI_SSID = "device";
const char* WIFI_PASSWORD = "device90";

// =========================
// Device Configuration
// =========================

const char* DEVICE_NAME = "ESP8266-01";

constexpr uint8_t LED_PIN = D4;
constexpr uint16_t HTTP_PORT = 80;
constexpr uint16_t UDP_PORT = 4210;

ESP8266WebServer server(HTTP_PORT);
WiFiUDP udp;

bool ledStatus = false;

// UDP receive buffer
char udpBuffer[128];

// =========================
// LED
// =========================

void setLed(bool state) {
  ledStatus = state;

  // Built-in ESP8266 LED is active LOW
  digitalWrite(LED_PIN, state ? LOW : HIGH);

  Serial.print("LED: ");
  Serial.println(state ? "ON" : "OFF");
}

// =========================
// HTTP Handlers
// =========================

void handleStatus() {
  String response = "{";
  response += "\"device\":\"";
  response += DEVICE_NAME;
  response += "\",";
  response += "\"ip\":\"";
  response += WiFi.localIP().toString();
  response += "\",";
  response += "\"chipId\":\"";
  response += String(ESP.getChipId(), HEX);
  response += "\",";
  response += "\"status\":\"";
  response += ledStatus ? "ON" : "OFF";
  response += "\"";
  response += "}";

  server.send(200, "application/json", response);
}

void handleLedOn() {
  setLed(true);

  server.send(
    200,
    "application/json",
    "{\"status\":\"ON\"}"
  );
}

void handleLedOff() {
  setLed(false);

  server.send(
    200,
    "application/json",
    "{\"status\":\"OFF\"}"
  );
}

void handleNotFound() {
  server.send(
    404,
    "application/json",
    "{\"error\":\"Not found\"}"
  );
}

// =========================
// UDP Discovery Response
// =========================

void sendDiscoveryResponse(IPAddress remoteIP, uint16_t remotePort) {

  String response = "{";
  response += "\"device\":\"";
  response += DEVICE_NAME;
  response += "\",";
  response += "\"ip\":\"";
  response += WiFi.localIP().toString();
  response += "\",";
  response += "\"port\":";
  response += String(HTTP_PORT);
  response += ",";
  response += "\"chipId\":\"";
  response += String(ESP.getChipId(), HEX);
  response += "\",";
  response += "\"status\":\"";
  response += ledStatus ? "ON" : "OFF";
  response += "\"";
  response += "}";

  udp.beginPacket(remoteIP, remotePort);
  udp.write(response.c_str());
  udp.endPacket();

  Serial.print("Discovery response sent to: ");
  Serial.println(remoteIP);
}

// =========================
// UDP Discovery Handler
// =========================

void handleUdpDiscovery() {

  int packetSize = udp.parsePacket();

  if (packetSize <= 0) {
    return;
  }

  int length = udp.read(udpBuffer, sizeof(udpBuffer) - 1);

  if (length <= 0) {
    return;
  }

  udpBuffer[length] = '\0';

  Serial.print("UDP packet from ");
  Serial.print(udp.remoteIP());
  Serial.print(":");
  Serial.print(udp.remotePort());
  Serial.print(" -> ");
  Serial.println(udpBuffer);

  if (strcmp(udpBuffer, "DISCOVER_ESP") == 0) {

    sendDiscoveryResponse(
      udp.remoteIP(),
      udp.remotePort()
    );
  }
}

// =========================
// Wi-Fi Connection
// =========================

void connectWiFi() {

  Serial.println();
  Serial.print("Connecting to: ");
  Serial.println(WIFI_SSID);

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi connected!");

  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  Serial.print("Gateway: ");
  Serial.println(WiFi.gatewayIP());

  Serial.print("Subnet: ");
  Serial.println(WiFi.subnetMask());

  Serial.print("MAC Address: ");
  Serial.println(WiFi.macAddress());

  Serial.print("Chip ID: ");
  Serial.println(String(ESP.getChipId(), HEX));
}

// =========================
// Setup
// =========================

void setup() {

  Serial.begin(9600);
  delay(100);

  // LED
  pinMode(LED_PIN, OUTPUT);
  setLed(false);

  // Wi-Fi
  connectWiFi();

  // UDP
  if (udp.begin(UDP_PORT)) {
    Serial.print("UDP Discovery started on port: ");
    Serial.println(UDP_PORT);
  } else {
    Serial.println("UDP initialization failed!");
  }

  // HTTP
  server.on("/status", HTTP_GET, handleStatus);
  server.on("/ledon", HTTP_GET, handleLedOn);
  server.on("/ledoff", HTTP_GET, handleLedOff);

  server.onNotFound(handleNotFound);

  server.begin();

  Serial.print("HTTP server started on port: ");
  Serial.println(HTTP_PORT);
}

// =========================
// Loop
// =========================

void loop() {

  server.handleClient();

  handleUdpDiscovery();
}