# Create Settings Page UI

Implement a settings screen with the requested options, following the visual style provided in the reference image.

## Proposed Changes

### UI Components

#### [NEW] [SettingTab.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/tab/SettingTab.kt)
- Create a new `SettingTab` composable.
- Define a `SettingItem` data class to represent each setting option.
- Implement a `SettingSection` composable to group items with a header.
- Implement a `SettingRow` composable for individual list items, featuring:
    - An icon on the left.
    - Title text.
    - A chevron-right icon on the right.
    - Clickable behavior.
- Organize items into logical sections:
    - **Connectivity & Devices**: Wifi setting, Manage devices, Search Devices.
    - **App Preferences**: Theme color.
    - **Support & Info**: Privacy, About us.
- Use `AppPalette` and `AppFont` for consistency with the existing design.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/screen/HomeScreen.kt)
- Replace the placeholder `BaseContent("Setting", innerPadding)` with the new `SettingTab(innerPadding)`.

## Verification Plan

### Manual Verification
- Deploy the app to a device or emulator.
- Navigate to the "Setting" tab using the bottom navigation.
- Verify that all requested options are displayed and grouped correctly.
- Ensure the UI matches the reference image's style (rounded corners, white cards, spacing).
