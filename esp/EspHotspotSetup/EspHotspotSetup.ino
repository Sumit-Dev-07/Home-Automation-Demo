#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <WiFiUdp.h>
#include <EEPROM.h>

// Include organized modules
#include "Config.h"
#include "Device.h"
#include "PinMap.h"
#include "Persistence.h"
#include "DeviceManager.h"
#include "WebHandlers.h"
#include "DiscoveryService.h"
#include "SyncLogic.h"

// Define Global Objects and Variables
namespace Config {
  char AP_SSID[32]     = "Smart Hub";
  char AP_PASSWORD[64] = "esp12345678";
}

Device devices[MAX_DEVICES];
ESP8266WebServer server(Config::HTTP_PORT);
WiFiUDP udp;
char udpBuffer[128];

// =========================
// Setup
// =========================

void setup() {
  Serial.begin(115200);
  Serial.println("\n[System] Initializing...");

  EEPROM.begin(Config::EEPROM_SIZE);
  loadConfig();

  // WiFi Access Point Setup
  WiFi.mode(WIFI_AP);
  WiFi.softAPConfig(Config::LOCAL_IP, Config::GATEWAY, Config::SUBNET);
  WiFi.softAP(Config::AP_SSID, Config::AP_PASSWORD);

  Serial.println("[WiFi] AP Started");
  Serial.print("[WiFi] IP Address: ");
  Serial.println(WiFi.softAPIP());

  // Discovery Service
  udp.begin(Config::UDP_PORT);

  // Initialize Hardware Pins for Active Devices
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

  // HTTP Server Routes
  server.on("/status", HTTP_GET, handleStatus);
  server.on("/relay/toggle", HTTP_GET, handleRelayToggle);
  server.on("/device/add", HTTP_GET, handleAddDevice);
  server.on("/device/remove", HTTP_GET, handleRemoveDevice);
  server.on("/wifi/update", HTTP_GET, handleUpdateWifi);

  server.begin();
  Serial.println("[HTTP] Server Ready");
}

// =========================
// Main Loop
// =========================

void loop() {
  server.handleClient();
  handleUdpDiscovery();
  handlePhysicalSwitches();
}
