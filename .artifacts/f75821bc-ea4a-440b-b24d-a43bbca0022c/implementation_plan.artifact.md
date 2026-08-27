# Reusable AppText Component Implementation Plan

Create a reusable `AppText` composable that provides a consistent way to use the project's custom font families (Onest) and standard typography.

## User Review Required

> [!IMPORTANT]
> The new `AppText` will allow passing a `FontFamily`. I will use the `Onest` fonts defined in `AppFont.kt` as the default or as easily accessible options.

## Proposed Changes

### UI Components

#### [MODIFY] [AppText.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/components/core/AppText.kt)

- Add a generic `AppText` composable.
- Support parameters: `text`, `modifier`, `style`, `color`, `textAlign`, `maxLines`, `overflow`, and `fontFamily`.
- Refactor existing `TitleText`, `MediumTitleText`, and `ErrorTextInputField` to use `AppText` for consistency.

## Verification Plan

### Manual Verification
- I will create a Compose Preview in `AppText.kt` to demonstrate `AppText` with different font families (Light, Regular, Medium, Bold).
- I will verify the rendering using `render_compose_preview`.
