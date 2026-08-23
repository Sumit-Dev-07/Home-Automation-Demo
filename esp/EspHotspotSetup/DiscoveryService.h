#ifndef DISCOVERYSERVICE_H
#define DISCOVERYSERVICE_H

#include <WiFiUdp.h>
#include "WebHandlers.h"

extern WiFiUDP udp;
extern char udpBuffer[128];

void handleUdpDiscovery() {
  int packetSize = udp.parsePacket();
  if (packetSize <= 0) return;
  int length = udp.read(udpBuffer, sizeof(udpBuffer) - 1);
  if (length <= 0) return;
  udpBuffer[length] = '\0';

  if (strcmp(udpBuffer, "DISCOVER_ESP") == 0) {
    String response = buildStatusJson();
    udp.beginPacket(udp.remoteIP(), udp.remotePort());
    udp.write(response.c_str());
    udp.endPacket();
  }
}

#endif
