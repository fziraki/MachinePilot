# MachinePilot

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose_BOM-2026.02.01-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/API-26%2B-4CAF50)](https://developer.android.com/studio/releases/platforms)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Industrial HMI simulator for Android — real-time telemetry, diagnostics, and alerts on a simulated machine dashboard.

<!-- ![App Screenshot](docs/screenshots/dashboard.png) -->

## Features

- **Live Telemetry** — simulated CAN bus data: RPM, speed, fuel, engine temp, hydraulic pressure
- **GPS & Battery** — location tracking and battery health monitoring
- **Health Diagnostics** — real-time analysis with severity-graded alerts (INFO / WARNING / CRITICAL)
- **Emergency Stop** — simulated emergency stop with state overlay and banner
- **Industrial HMI UI** — dark theme, gauge cards, system health panel, active alerts list with Material 3

## Architecture

```
:app ───────────────────────────────────────────────── :machine-sdk
 │                                                      │
 ├── feature/dashboard                                  │
 │   ├── DashboardScreen.kt         UI composables      │
 │   ├── DashboardViewModel.kt      state container      │
 │   ├── DashboardContract.kt       state/action defs    │
 │   └── Extensions.kt              formatting helpers   │
 │                                                      │
 ├── domain                                             │
 │   ├── model/                  Alert, Telemetry, etc.  │
 │   ├── repository/             MachineRepository       │
 │   └── analyzer/               MachineAnalyzer         │
 │                                                      │
 ├── data                                               │
 │   ├── repository/             MachineRepositoryImpl   │
 │   ├── mapper/                 TelemetryMapper         │
 │   └── di/                     Hilt DataModule         │
 │                                                      │
 └── designsystem                                       │
     └── theme/                 Color, Theme, Typography │
                                                      │
                                         ┌──────────────────────┐
                                         │  MachineClient        │
                                         │  (interface)          │
                                         ├──────────────────────┤
                                         │  FakeMachineClient    │
                                         │  - CAN simulation     │
                                         │  - GPS simulation     │
                                         │  - Battery simulation │
                                         └──────────────────────┘
```

### Data Flow

```
FakeMachineClient ──Flow──▶ MachineRepositoryImpl ──Flow──▶ DashboardViewModel ──State──▶ DashboardScreen
      │                                                       │
      │  emergencyStop()                                      │  onAction()
      └──────────────────▶ MachineRepository ◄────────────────┘
```

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVI (State / Action / Event) |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| SDK | Android library module (`:machine-sdk`) |
| Compile SDK | 37 (Android 15) |
| Min SDK | 26 |

## Screenshots

| View | File |
|---|---|
| Dashboard | `docs/screenshots/dashboard.png` |
| Emergency Stop | `docs/screenshots/emergency_stop.png` |

## License

MIT — see [LICENSE](LICENSE).
