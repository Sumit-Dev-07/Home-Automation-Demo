#ifndef DEVICE_H
#define DEVICE_H

#include <Arduino.h>

struct Device {
  char name[20];
  uint8_t relayPin;
  uint8_t switchPin; // 255 if no sync switch
  bool state;
  bool lastSwitchState;
  bool active;
};

constexpr size_t MAX_DEVICES = 8;
extern Device devices[MAX_DEVICES];

#endif
