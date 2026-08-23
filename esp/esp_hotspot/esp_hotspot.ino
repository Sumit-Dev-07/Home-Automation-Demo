#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiUdp.h>

// =========================
// Wi-Fi Hotspot (AP) Configuration
// =========================

const char* AP_SSID     = "ESP8266-IoT";   // Name your phone will see
const char* AP_PASSWORD = "esp12345678";   // Must be 8+ characters, or "" for open network

IPAddress local_IP(192, 168, 4, 1);
IPAddress gateway(192, 168, 4, 1);
IPAddress subnet(255, 255, 255, 0);

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
char udpBuffer[128];

// =========================
// LED
// =========================

void setLed(bool state) {
  ledStatus = state;
  digitalWrite(LED_PIN, state ? LOW : HIGH); // active LOW
  Serial.print("LED: ");
  Serial.println(state ? "ON" : "OFF");
}

// =========================
// HTTP Handlers
// =========================

void handleRoot() {
  // Simple webpage so you can control it from a browser too
  String html = "<html><body style='font-family:sans-serif;text-align:center;margin-top:50px'>";
  html += "<h2>" + String(DEVICE_NAME) + "</h2>";
  html += "<p>Status: <b>" + String(ledStatus ? "ON" : "OFF") + "</b></p>";
  html += "<a href='/ledon'><button style='padding:15px 30px;font-size:18px'>Turn ON</button></a> ";
  html += "<a href='/ledoff'><button style='padding:15px 30px;font-size:18px'>Turn OFF</button></a>";
  html += "</body></html>";
  server.send(200, "text/html", html);
}

void handleStatus() {
  String response = "{";
  response += "\"device\":\"" + String(DEVICE_NAME) + "\",";
  response += "\"ip\":\"" + WiFi.softAPIP().toString() + "\",";
  response += "\"chipId\":\"" + String(ESP.getChipId(), HEX) + "\",";
  response += "\"status\":\"" + String(ledStatus ? "ON" : "OFF") + "\"";
  response += "}";
  server.send(200, "application/json", response);
}

void handleLedOn() {
  setLed(true);
  server.send(200, "application/json", "{\"status\":\"ON\"}");
}

void handleLedOff() {
  setLed(false);
  server.send(200, "application/json", "{\"status\":\"OFF\"}");
}

void handleNotFound() {
  server.send(404, "application/json", "{\"error\":\"Not found\"}");
}

// =========================
// UDP Discovery
// =========================

void sendDiscoveryResponse(IPAddress remoteIP, uint16_t remotePort) {
  String response = "{";
  response += "\"device\":\"" + String(DEVICE_NAME) + "\",";
  response += "\"ip\":\"" + WiFi.softAPIP().toString() + "\",";
  response += "\"port\":" + String(HTTP_PORT) + ",";
  response += "\"chipId\":\"" + String(ESP.getChipId(), HEX) + "\",";
  response += "\"status\":\"" + String(ledStatus ? "ON" : "OFF") + "\"";
  response += "}";

  udp.beginPacket(remoteIP, remotePort);
  udp.write(response.c_str());
  udp.endPacket();

  Serial.print("Discovery response sent to: ");
  Serial.println(remoteIP);
}

void handleUdpDiscovery() {
  int packetSize = udp.parsePacket();
  if (packetSize <= 0) return;

  int length = udp.read(udpBuffer, sizeof(udpBuffer) - 1);
  if (length <= 0) return;

  udpBuffer[length] = '\0';

  Serial.print("UDP packet from ");
  Serial.print(udp.remoteIP());
  Serial.print(":");
  Serial.print(udp.remotePort());
  Serial.print(" -> ");
  Serial.println(udpBuffer);

  if (strcmp(udpBuffer, "DISCOVER_ESP") == 0) {
    sendDiscoveryResponse(udp.remoteIP(), udp.remotePort());
  }
}

// =========================
// Access Point Setup
// =========================

void startAccessPoint() {
  Serial.println();
  Serial.println("Starting Access Point...");

  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(local_IP, gateway, subnet);
  WiFi.softAP(AP_SSID, AP_PASSWORD);

  Serial.print("AP SSID: ");
  Serial.println(AP_SSID);
  Serial.print("AP IP address: ");
  Serial.println(WiFi.softAPIP());
  Serial.print("Chip ID: ");
  Serial.println(String(ESP.getChipId(), HEX));
}

// =========================
// Setup
// =========================

void setup() {
  Serial.begin(9600);
  delay(100);

  pinMode(LED_PIN, OUTPUT);
  setLed(false);

  startAccessPoint();

  if (udp.begin(UDP_PORT)) {
    Serial.print("UDP Discovery started on port: ");
    Serial.println(UDP_PORT);
  } else {
    Serial.println("UDP initialization failed!");
  }

  server.on("/", handleRoot);
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