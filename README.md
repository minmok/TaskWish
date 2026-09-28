# TaskWish

TaskWish is an Android productivity game: you complete real-world tasks to earn **Favor**, then spend Favor on randomized **Wish/Gacha** pulls for self-defined rewards.

## What it does

- **Task-to-Favor loop**: Create tasks and mark them done to earn Favor based on tier:
  - Easy: +10 Favor
  - Medium: +20 Favor
  - Hard: +50 Favor
- **Wish/Gacha spending**:
  - 1 pull costs 10 Favor
  - 5 pulls cost 50 Favor (executed as sequential pulls so pity carries)
- **Rarity system**: Rewards are configured as 3★ / 4★ / 5★.
- **Pity & guarantee behavior (implemented rules)**:
  - Base 5★ chance: 0.5%
  - Soft pity starts at pull 25
  - 5★ hard pity at pull 30
  - 4★ hard pity at 10 pulls without 4★+
  - Featured 5★ handling with guaranteed-next-featured after losing a featured roll
- **Featured 5★ setup**: Configure a current featured 5★ reward in-app.
- **Inventory tracking**: Won rewards are tracked with a persistent `timesWon` count.
- **Daily login rewards**:
  - 7-day bonus schedule: 10 / 15 / 20 / 25 / 30 / 40 / 60 Favor
  - One-day grace period for streak continuity
  - +15 Favor welcome-back bonus after a broken streak
- **Local persistence only**: Data is stored locally via Room (no backend/API setup required).

### Development helpers (for local testing)

The app currently includes developer-facing helper actions in the Wish screen:

- Add +5000 Favor
- Reset pity counters
- Free Wish x1 / Free Wish x5

These are for development/testing convenience, not core player progression.

## Architecture at a glance

### Project structure

```text
TaskWish/
├─ settings.gradle.kts                         # Root project ("Gacha Productive App"), includes :app
├─ app/build.gradle.kts                        # Android app module config (SDK, Compose, Room, deps)
├─ app/src/main/AndroidManifest.xml            # MainActivity launcher + app theme
├─ app/src/main/java/com/example/gachaproductiveapp/
│  ├─ MainActivity.kt                          # App root, welcome gate, tab navigation, daily login dialog
│  ├─ data/                                    # Room entities, DAOs, DB, type converters
│  ├─ repository/GameRepository.kt             # Business logic: tasks, Favor, pulls, daily login, inventory updates
│  ├─ logic/GachaEngine.kt                     # Pull probabilities, pity, guarantee logic
│  ├─ logic/DailyLoginManager.kt               # Streak and login bonus rules
│  ├─ viewmodel/MainViewModel.kt               # StateFlows + UI actions delegated to repository
│  └─ ui/                                      # Compose screens and theme
└─ app/src/{test,androidTest}/...              # Local unit/instrumented test source sets
```

### Key layers

- **UI (Jetpack Compose + Material 3)**: Screen rendering and user interactions.
- **ViewModel (AndroidX lifecycle/ViewModel)**: Exposes state via `StateFlow` and invokes repository operations.
- **Repository**: Coordinates DAOs and applies game logic.
- **Logic modules**: Deterministic gameplay rules for gacha and daily login.
- **Data (Room + KSP)**: Local persistence for tasks, rewards, and singleton user state.

## Tech stack

- Kotlin + JVM toolchain 11
- Android Gradle Plugin 8.8.2
- Gradle Wrapper 9.5.0
- Jetpack Compose (BOM 2024.12.01) + Material 3
- AndroidX Lifecycle / ViewModel
- Room 2.6.1 + KSP
- Coil (image loading)

## Prerequisites

- Android Studio (recent version with AGP 8.8.x / Kotlin 2.0 support), or command-line Android SDK tooling
- JDK 11
- Android device/emulator with **API 30+** (minSdk = 30)

## Setup and run

### Android Studio

1. Open the repository root in Android Studio:
   `/home/runner/work/TaskWish/TaskWish`
2. Let Gradle sync complete.
3. Run the `app` configuration on an emulator/device (API 30+).

### Command line

From the repository root:

```bash
./gradlew assembleDebug
```

Install/run the generated debug build through Android Studio or your usual `adb` workflow.

## Testing

The repository contains both local unit test and instrumented test source sets.

Run from repository root:

```bash
./gradlew test
```

For instrumented tests (requires connected emulator/device, API 30+):

```bash
./gradlew connectedAndroidTest
```

## Gameplay flow

1. Enter a username on the welcome screen.
2. Add tasks with a tier (Easy/Medium/Hard).
3. Complete tasks to earn Favor.
4. Configure reward pool entries (including optional featured 5★).
5. Spend Favor on Wish pulls (x1 or x5).
6. Review pulled rewards and cumulative `timesWon` in Inventory.
7. Claim daily login bonuses to maintain streak progression.

## Gacha rules (implementation)

| Rule | Implemented behavior |
|---|---|
| Pull cost | 10 Favor (single), 50 Favor (five pull) |
| Five-star base chance | 0.5% before soft pity |
| Soft pity | Starts at pull 25 (chance ramps each pull) |
| Five-star hard pity | Guaranteed by pull 30 |
| Four-star hard pity | If 10 pulls pass without 4★+, next eligible result upgrades to 4★ |
| Featured 5★ logic | 5★ roll is featured if guaranteed or 50/50 succeeds |
| Featured guarantee state | Losing featured sets guarantee for next 5★ |
| Five-pull behavior | Executed as 5 sequential pulls (pity/guarantee evolve per pull) |

## Development notes

- Current package/application ID: `com.example.gachaproductiveapp`
- Current root project name: `Gacha Productive App`
- Room database uses `fallbackToDestructiveMigration()` in `AppDatabase`
- This appears to be an early-stage app (simple example tests and evolving feature set)

## License

No license is currently declared in this repository.
