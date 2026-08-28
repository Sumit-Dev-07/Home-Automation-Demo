# Implementation Plan - Add CommonTopAppBar to Search Devices Screen

The goal is to replace the custom `TopAppBar` in `SearchDeviceScreen.kt` with the newly created `CommonTopAppBar` component to ensure UI consistency across the app.

## User Review Required

> [!IMPORTANT]
> The `CommonTopAppBar` uses the primary theme color (Orange) for its background, whereas the current custom `TopAppBar` was transparent. This will change the visual appearance of the top of the screen to a solid orange bar.
>
> The "Welcome Back" and Wi-Fi SSID information currently in the top bar will be replaced by a standard title (e.g., "Search Devices").

## Proposed Changes

### UI Components

#### [MODIFY] [SearchDeviceScreen.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/search/SearchDeviceScreen.kt)
- Import `CommonTopAppBar`.
- Replace the `TopAppBar` in `SearchDeviceContent` with `CommonTopAppBar`.
- Use "Search Devices" as the title for the `CommonTopAppBar`.
- Remove the redundant status bar background `Box` and adjust the layout if necessary.
- Keep the dynamic title and description in the body as they are central to the scanning UI.

## Verification Plan

### Manual Verification
- Deploy the app to a device or emulator.
- Navigate to the Search Devices screen (from Home -> Find Devices).
- Verify that the `CommonTopAppBar` is displayed correctly with the "Search Devices" title and a back button.
- Verify that clicking the back button works as expected.
- Verify that the status bar area looks correct with the new top bar.
