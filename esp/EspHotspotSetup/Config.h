#ifndef CONFIG_H
#define CONFIG_H

#include <ESP8266WiFi.h>

namespace Config {
  extern char AP_SSID[32];
  extern char AP_PASSWORD[64];

  const IPAddress LOCAL_IP(192, 168, 4, 1);
  const IPAddress GATEWAY(192, 168, 4, 1);
  const IPAddress SUBNET(255, 255, 255, 0);

  constexpr char DEVICE_NAME[]  = "Smart Board";
  constexpr uint16_t HTTP_PORT  = 80;
  constexpr uint16_t UDP_PORT   = 4210;

  constexpr int EEPROM_SIZE = 512;
  constexpr int MAGIC_NUMBER = 0xABCD;
}

#endif
