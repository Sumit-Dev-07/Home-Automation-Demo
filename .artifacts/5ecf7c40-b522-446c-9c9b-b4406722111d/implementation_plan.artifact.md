# Implement WiFi check and device scanning in LauncherScreen

Automate the initial connection process by checking for WiFi connectivity, scanning for ESP devices, and automatically selecting the first discovered device before proceeding to the home screen.

## Proposed Changes

### UI Components

#### [MODIFY] [LauncherScreen.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/common/screen/LauncherScreen.kt)
- Inject `HomeViewModel` using `hiltViewModel()`.
- Add `LaunchedEffect` to check WiFi connectivity on start.
- If WiFi is connected, trigger device discovery via `viewModel.findEspDevices()`.
- Observe `scanState` from `HomeViewModel`.
- On successful discovery of at least one device:
    - Update `ApiPath.LOCAL_WIFI_IP_URL` with the IP of the first device.
    - Display the name of the connected device.
    - Navigate to the main screen.
- Handle error/no-device cases by navigating to the main screen after a short delay to allow for manual setup.
- Update UI to show current status (e.g., "Scanning for devices...", "Device found: ...").

## Verification Plan

### Automated Tests
- Build and run the app to ensure no compilation errors.
- Verify Hilt injection is working for `HomeViewModel`.

### Manual Verification
1. Open the app on a device connected to the same WiFi as an ESP device.
2. Observe the Launcher screen:
    - It should show "Scanning for devices...".
    - Once a device is found, it should show "Device found: [Name]".
    - It should automatically navigate to the Home screen.
3. Verify that the Home screen is now communicating with the discovered device (status should be fetched from that IP).
4. Test with WiFi disconnected to ensure it handles the "WiFi not connected" state gracefully and navigates to the main screen.
