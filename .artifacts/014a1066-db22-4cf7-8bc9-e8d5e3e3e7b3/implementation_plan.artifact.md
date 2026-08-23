# Plan: Automatic Connection for ESP Hotspot

We are scanning the network because the app currently relies on UDP discovery to find ESP devices on a local network. However, when the ESP is in **Hotspot (AP) Mode**, it always has a fixed IP address (typically `192.168.4.1`).

This plan will automate the connection to the ESP hotspot when detected, bypassing the need for manual scanning.

## Proposed Changes

### [Component: Data]

#### [MODIFY] [ApiPath.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/data/ApiPath.kt)
- Add constants for default ESP Hotspot SSID and IP address.

### [Component: UI]

#### [MODIFY] [HomeTab.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/tab/HomeTab.kt)
- Update the `NetworkCallback` or add a `LaunchedEffect` to detect when the phone connects to `ESP8266-IoT`.
- Automatically set the target IP to `192.168.4.1` if the hotspot is detected.
- This will skip the "Find Devices" requirement and immediately fetch device status.

## Verification Plan

### Manual Verification
1. Connect the phone to the ESP8266 hotspot (SSID: `ESP8266-IoT`).
2. Open the app.
3. Verify that the app automatically detects the device and shows the controls (Light 1, etc.) without needing to click "Scan".
4. Verify that the IP address shown is `192.168.4.1`.
5. Disconnect from the hotspot and connect to a regular WiFi; verify that the "Scan" option appears again.
