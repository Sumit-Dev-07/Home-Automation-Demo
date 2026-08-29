# Refactor AppText to Object-Based Component

Refactor the `AppText` component to provide a structured API using sub-components for different font weights, ensuring consistent usage of the `AppFont` family.

## Proposed Changes

### UI Components

#### [MODIFY] [AppText.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/components/core/AppText.kt)
- Convert `AppText` from a top-level function to an `object`.
- Implement a private base composable `TextBase` to handle common logic.
- Add the following composable functions to the `AppText` object:
    - `Light`: Uses `AppFont.onestLight`.
    - `Normal`: Uses `AppFont.onestRegular`.
    - `Medium`: Uses `AppFont.onestMedium`.
    - `SemiBold`: Uses `AppFont.onestSemiBold`.
    - `Bold`: Uses `AppFont.onestBold`.
- Each function will accept standard parameters like `text`, `modifier`, `color`, `fontSize`, `textAlign`, `maxLines`, and `overflow`.
- Retain existing helper components like `TitleText`, `MediumTitleText`, and `ErrorTextInputField` but refactor them to use the new `AppText` object methods.

## Verification Plan

### Automated Tests
- Run `app:assembleDebug` to ensure no compilation errors are introduced by the refactoring.

### Manual Verification
- Update the `AppTextFontsPreview` in `AppText.kt` to use the new `AppText.Normal`, `AppText.Medium`, etc., and verify the preview renders correctly in Android Studio.
- Verify that other screens (like `SettingTab`) still compile if they were using the previous `AppText` (or update them if necessary).
