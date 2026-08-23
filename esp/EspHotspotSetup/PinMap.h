#ifndef PINMAP_H
#define PINMAP_H

#include <Arduino.h>

uint8_t getPin(String pinName) {
  if (pinName == "D0") return 16;
  if (pinName == "D1") return 5;
  if (pinName == "D2") return 4;
  if (pinName == "D3") return 0;
  if (pinName == "D4") return 2;
  if (pinName == "D5") return 14;
  if (pinName == "D6") return 12;
  if (pinName == "D7") return 13;
  if (pinName == "D8") return 15;
  return 255;
}

String getPinName(uint8_t pin) {
  switch(pin) {
    case 16: return "D0";
    case 5:  return "D1";
    case 4:  return "D2";
    case 0:  return "D3";
    case 2:  return "D4";
    case 14: return "D5";
    case 12: return "D6";
    case 13: return "D7";
    case 15: return "D8";
    default: return "None";
  }
}

#endif
