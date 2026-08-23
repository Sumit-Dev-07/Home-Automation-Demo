# Sync Android App with ESP Hotspot Firmware

The `esp_hotspot.ino` firmware uses a specific set of HTTP endpoints and JSON structure for relay control and status reporting. The current Android implementation has some hardcoded endpoints and parsing logic that doesn't fully align with the firmware.

This plan will:
1. Update the data layer to use the correct relay control endpoints (`/relay/on?relay=...` and `/relay/off?relay=...`).
2. Refactor the repository and use cases to support dynamic relay control by name.
3. Clean up the ViewModel's status parsing to match the firmware's JSON structure.

## User Review Required

> [!IMPORTANT]
> The relay control logic is being refactored from hardcoded `ledOn`/`ledOff` methods to a generic `toggleRelay(name, isOn)` approach. This is more scalable and matches the firmware's requirement of passing the relay name as a query parameter.

## Proposed Changes

### Data Layer

#### [MODIFY] [HomeDataSource.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/data/datasource/HomeDataSource.kt)
- Replace specific LED methods with `toggleRelay(name: String, isOn: Boolean)`.

#### [MODIFY] [HomeDataSourceImpl.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/data/datasource/HomeDataSourceImpl.kt)
- Implement `toggleRelay` using the `/relay/on?relay=...` and `/relay/off?relay=...` endpoints.

### Domain Layer

#### [MODIFY] [HomeRepository.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/domain/repository/HomeRepository.kt)
- Update interface to use `toggleRelay(name: String, isOn: Boolean)`.

#### [MODIFY] [HomeRepositoryImpl.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/domain/repository/HomeRepositoryImpl.kt)
- Update implementation to delegate to the new `toggleRelay` in `HomeDataSource`.

#### [MODIFY] [HomeUseCase.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/domain/usecase/HomeUseCase.kt)
- Replace `ledOn`, `ledOff`, etc., with a single `toggleRelay(name: String, isOn: Boolean)` method.

### UI Layer

#### [MODIFY] [HomeViewModel.kt](file:///C:/Users/sumit/Documents/WorkGround/Home-Automation-Demo/app/src/main/java/com/app/iot/ui/features/home/viewmodel/HomeViewModel.kt)
- Update `controlLed` to use the new `toggleRelay` use case.
- Simplify `parseStatusResponse` to focus on the `relays` array provided by the firmware.

## Verification Plan

### Automated Tests
- Since this project relies on local network communication with hardware, unit tests for parsing logic will be prioritized.

### Manual Verification
1. Deploy the app to an Android device.
2. Connect to the `ESP8266-IoT` hotspot.
3. Scan for devices in the app.
4. Verify that two relays (`relay1`, `relay2`) appear.
5. Toggle each relay and verify (via Logcat or observing the ESP) that the correct `/relay/on?relay=...` calls are made.
