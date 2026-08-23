# Project Development: Home Automation (Local-First)

This document outlines the feature roadmap and technical requirements for the Home Automation system, focusing on local communication between Android devices and ESP8266-based hardware.

## 1. Device Control
Primary interface for interacting with hardware. Commands are sent via HTTP/UDP over the local network.

| Feature | Description | Status |
| :--- | :--- | :--- |
| **On/Off Toggle** | Basic power control for relays/switches. | [x] Implemented |
| **Speed Control** | PWM-based control for fans (slider or stepped). | [ ] Planned |
| **Brightness** | PWM-based dimming for lights with percentage display. | [ ] Planned |
| **Color Control** | RGB/RGBW control for LED strips (Color picker). | [ ] Planned |
| **Instant Response** | Low-latency local communication (no cloud hops). | [x] Implemented |

## 2. Scheduling
Time-based automation that must be resilient to power/network outages.

> [!IMPORTANT]
> Schedules must be stored and executed on the **Device Firmware** to ensure they fire even if the Android app is closed or the phone is offline.

*   **One-time/Recurring:** Support for daily, weekly, or specific day triggers.
*   **Astronomical Triggers:** Sunrise/Sunset based on local coordinates (pushed from app).
*   **Countdown Timers:** "Turn off in X minutes" logic handled on-device.
*   **Time Sync:** Device periodically syncs NTP time via the local gateway.

## 3. Power Consumption & Monitoring
Data visualization and cost analysis (requires hardware with energy monitoring ICs).

*   **Real-time Monitoring:** Voltage, Current, and Wattage display.
*   **Historical Data:** Daily/Weekly/Monthly usage graphs stored in the Android app's local database.
*   **Cost Calculator:** Tariff-based estimation.
*   **Safety Alerts:** Notifications for over-current or unexpected power draw.

## 4. Device & Network Settings
The foundation for device personalization and provisioning.

| Feature | Requirement | Status |
| :--- | :--- | :--- |
| **Rename Device** | User-defined label (e.g., "Living Room ESP"). | [ ] Planned |
| **Rename Relays** | Individual labels for each channel (e.g., "Fan", "Main Light"). | [ ] Planned |
| **WiFi Config** | Change SSID/Password from the app. | [ ] Planned |
| **Admin Security** | PIN/Password protection for settings changes. | [ ] Planned |
| **OTA Updates** | Local firmware updates via LAN/APK bundle. | [ ] Planned |

## 5. Ecosystem & Experience (Suggested Additions)
Enhancements to make the system more robust and user-friendly.

### Discovery & Organization
*   **UDP Discovery:** Auto-find devices on the network. `[x] Implemented`
*   **Rooms & Groups:** Logical organization of devices (e.g., "Kitchen", "Upstairs").
*   **Scenes:** Multi-device presets (e.g., "Movie Night").

### User Experience
*   **Multi-user Access:** Multiple phones controlling the same local hardware.
*   **Home Screen Widgets:** Quick-toggle for favorite devices.
*   **Local Notifications:** Alerts for device status (e.g., "Iron left ON for 30m").

### Security & Reliability
*   **Encrypted LAN Traffic:** Preventing local sniffing of commands.
*   **Backup/Restore:** Export device configurations to a file.
*   **Factory Reset Flow:** Physical button or AP-mode reset for provisioning.

## Technical Stack
*   **Firmware:** ESP8266 (Arduino/C++), LittleFS (Storage), ESP8266WebServer, WiFiUDP.
*   **Android:** Kotlin, Jetpack Compose, Retrofit (Local API), Room (Local Database), Hilt (DI), Coroutines/Flow.
*   **Communication:** REST over HTTP (Commands), UDP (Discovery), mDNS (Optional).
