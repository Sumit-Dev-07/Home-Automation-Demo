#ifndef PERSISTENCE_H
#define PERSISTENCE_H

#include <EEPROM.h>
#include "Config.h"
#include "Device.h"

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

#endif
