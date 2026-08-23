#ifndef SYNCLOGIC_H
#define SYNCLOGIC_H

#include "Device.h"
#include "DeviceManager.h"

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

#endif
