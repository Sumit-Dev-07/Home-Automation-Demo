#ifndef DEVICEMANAGER_H
#define DEVICEMANAGER_H

#include <Arduino.h>
#include "Device.h"

void applyDeviceState(int i) {
  if (i < 0 || i >= MAX_DEVICES || !devices[i].active) return;
  digitalWrite(devices[i].relayPin, devices[i].state ? LOW : HIGH); // Active Low
}

void setDevice(int i, bool state) {
  if (i < 0 || i >= MAX_DEVICES || !devices[i].active) return;
  devices[i].state = state;
  applyDeviceState(i);
  Serial.printf("[Device] %s -> %s\n", devices[i].name, state ? "ON" : "OFF");
}

#endif
