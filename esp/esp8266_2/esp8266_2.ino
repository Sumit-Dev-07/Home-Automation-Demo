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

constexpr uint8_t LED1_PIN = D4;
constexpr uint8_t LED2_PIN = D5;
constexpr uint16_t HTTP_PORT = 80;
constexpr uint16_t UDP_PORT = 4210;

ESP8266WebServer server(HTTP_PORT);
WiFiUDP udp;

bool led1Status = false;
bool led2Status = false;

// UDP receive buffer
char udpBuffer[128];

// =========================
// LED
// =========================

void setLed1(bool state) {
    led1Status = state;

    // Built-in ESP8266 LED is active LOW
    digitalWrite(LED1_PIN, state ? LOW : HIGH);

    Serial.print("LED1: ");
    Serial.println(state ? "ON" : "OFF");
}

void setLed2(bool state) {
    led2Status = state;

    digitalWrite(LED2_PIN, state ? LOW : HIGH);

    Serial.print("LED2: ");
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
    response += "\"led1\":\"";
    response += led1Status ? "ON" : "OFF";
    response += "\",";
    response += "\"led2\":\"";
    response += led2Status ? "ON" : "OFF";
    response += "\"";
    response += "}";

    server.send(200, "application/json", response);
}

void handleLed1On() {
    setLed1(true);
    server.send(200, "application/json", "{\"led1\":\"ON\"}");
}

void handleLed1Off() {
    setLed1(false);
    server.send(200, "application/json", "{\"led1\":\"OFF\"}");
}

void handleLed2On() {
    setLed2(true);
    server.send(200, "application/json", "{\"led2\":\"ON\"}");
}

void handleLed2Off() {
    setLed2(false);
    server.send(200, "application/json", "{\"led2\":\"OFF\"}");
}

void handleAllOn() {
    setLed1(true);
    setLed2(true);
    server.send(200, "application/json", "{\"led1\":\"ON\",\"led2\":\"ON\"}");
}

void handleAllOff() {
    setLed1(false);
    setLed2(false);
    server.send(200, "application/json", "{\"led1\":\"OFF\",\"led2\":\"OFF\"}");
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
    response += "\"led1\":\"";
    response += led1Status ? "ON" : "OFF";
    response += "\",";
    response += "\"led2\":\"";
    response += led2Status ? "ON" : "OFF";
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

    // LEDs
    pinMode(LED1_PIN, OUTPUT);
    pinMode(LED2_PIN, OUTPUT);
    setLed1(false);
    setLed2(false);

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

    server.on("/led1on", HTTP_GET, handleLed1On);
    server.on("/led1off", HTTP_GET, handleLed1Off);

    server.on("/led2on", HTTP_GET, handleLed2On);
    server.on("/led2off", HTTP_GET, handleLed2Off);

    server.on("/allon", HTTP_GET, handleAllOn);
    server.on("/alloff", HTTP_GET, handleAllOff);

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