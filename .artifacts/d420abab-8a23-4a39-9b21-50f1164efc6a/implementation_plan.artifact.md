# Refactor to Package-by-Feature Architecture

Organize the `remind-me` Android project from a single-file structure (`MainActivity.kt`) into a clean **Package-by-Feature** architectural pattern.

## User Review Required

> [!NOTE]
> This refactoring will separate data models, UI components, and screens into dedicated packages (`feature/health` and `core/theme`) while keeping `MainActivity.kt` as a clean entry point.

## Proposed Changes

### Core Component
- Move/organize theme files under `com.example.remind_me.core.theme`.

### Health Feature (`com.example.remind_me.feature.health`)
- **[NEW]** `data/MetricItem.kt`: Data class for health metrics.
- **[NEW]** `ui/MyHealthScreen.kt`: Composable UI screen representing the "My Health" dashboard, search bar, metrics grid, and connected devices.

### Entry Point
- **[MODIFY]** `MainActivity.kt`: Simplify to only host `MyHealthApp` from the health feature.

## Verification Plan

### Automated Tests
- Run `gradle_build("app:assembleDebug")` to ensure compilation succeeds without errors.
