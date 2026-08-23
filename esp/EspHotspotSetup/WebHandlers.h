#ifndef WEBHANDLERS_H
#define WEBHANDLERS_H

#include <ESP8266WebServer.h>
#include "Device.h"
#include "DeviceManager.h"
#include "PinMap.h"
#include "Persistence.h"

extern ESP8266WebServer server;

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

#endif
