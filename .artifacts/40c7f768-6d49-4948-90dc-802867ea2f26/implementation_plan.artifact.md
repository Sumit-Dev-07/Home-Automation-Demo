# Create Custom Vertical ON/OFF Toggle and Integrate into Device Card

The goal is to create a custom vertical toggle switch as shown in the provided image and use it in the `DeviceItem` component on the home screen.

## Proposed Changes

### [Home Feature Components]

#### [NEW] [VerticalOnOffToggle.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/tab/components/VerticalOnOffToggle.kt)
- Create a new Composable `VerticalOnOffToggle`.
- Implement vertical sliding animation for the indicator.
- Implement color transitions for the indicator and text based on the state.
- Add a `@Preview` for the toggle.

#### [MODIFY] [HomeTab.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/tab/HomeTab.kt)
- Update `DeviceItem` to replace the standard `Switch` with the new `VerticalOnOffToggle`.
- Adjust the layout of `DeviceItem` to accommodate the vertical toggle if necessary.

## Verification Plan

### Automated Tests
- N/A (UI focused change)

### Manual Verification
- Deploy the app and check the `DeviceItem` on the Home tab.
- Interact with the new vertical toggle to ensure it animates correctly and updates the device state.
- Verify the colors match the design in both ON and OFF states.
