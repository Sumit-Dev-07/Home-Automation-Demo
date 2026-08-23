# Dynamic ESP Home Automation Setup

I have implemented a full setup for dynamic device management using ESP8266 and an Android app. This includes support for multiple nodes (D0-D8), two-way synchronization with physical switches, and on-the-fly configuration via the mobile app.

## Changes Made

### ESP Firmware ([esp_hotspot_full_setup.ino](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/esp_hotspot_full_setup.ino))
- **Dynamic Configuration**: Devices (relays) are no longer hardcoded. They can be added, named, and mapped to specific pins (D0-D8) dynamically.
- **EEPROM Storage**: All configurations (device names, pins, sync settings, and WiFi password) are persisted in EEPROM.
- **Two-Way Sync**: Implemented logic to support physical toggle switches. If a switch's state changes, the relay toggles, and the app state stays in sync.
- **New Endpoints**:
    - `/device/add`: Add a new relay with a custom name, pin, and optional sync pin.
    - `/device/remove`: Remove an existing device.
    - `/wifi/update`: Change the Access Point password.
    - `/relay/toggle`: Updated to support status-based switching.

### Professional ESP Firmware Structure
The firmware has been refactored into modular components for scalability:
- **[Config.h](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/Config.h)**: Centralized configuration for WiFi and system parameters.
- **[Device.h](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/Device.h)**: Definition of the `Device` structure and shared state.
- **[Persistence.h](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/Persistence.h)**: EEPROM logic to save/load dynamic configurations.
- **[WebHandlers.h](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/WebHandlers.h)**: Clear separation of HTTP API endpoints.
- **[DiscoveryService.h](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/DiscoveryService.h)**: Isolated UDP discovery logic.
- **[SyncLogic.h](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/SyncLogic.h)**: Logic for physical two-way switch synchronization.
- **[esp_hotspot_full_setup.ino](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/esp/esp_hotspot_full_setup/esp_hotspot_full_setup.ino)**: Clean entry point orchestrating all modules.

### Android Application
- **Device Management**:
    - **Add Device**: New FloatingActionButton and Dialog to add devices by name and pin.
    - **Remove Device**: Ability to delete devices directly from the home screen.
- **WiFi Configuration**:
    - **Change Password**: New settings option to update the ESP's AP password.
- **UI Enhancements**:
    - Updated `HomeTab.kt` with a modern look, including dropdowns for pin selection.
    - Added success/error feedback via Snackbars for all operations.
- **Data Layer**:
    - Updated `ApiService`, `HomeRepository`, and `HomeDataSource` to support the new ESP API.

## Verification Plan

### Manual Verification
1.  **ESP Setup**:
    - Flash `esp_hotspot_full_setup.ino` to an ESP8266.
    - Connect to the "ESP-Home-Sync" WiFi (password: `esp12345678`).
2.  **Android App**:
    - Run the app and scan for the device.
    - Tap the **+** button to add a device (e.g., "Fan" on pin D1).
    - Toggle the device from the app and verify the physical relay (or LED on D1).
    - If a physical switch is connected to a sync pin, toggle it and verify the app UI updates.
    - Go to WiFi settings (top right icon) and change the password. Verify the ESP restarts and requires the new password.
