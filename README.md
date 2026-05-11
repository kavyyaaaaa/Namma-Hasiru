# Namma Hasiru — Plantation Tracker

## How to Import into Android Studio

### Step 1: Open the Project
1. Open **Android Studio** (Arctic Fox or later recommended)
2. Click **File → Open**
3. Navigate to: `MM-Project/NammaHasiru/`
4. Select the `NammaHasiru` folder and click **Open**
5. Wait for Gradle sync to complete (may take a few minutes first time)

### Step 2: Set Up Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a project or select existing one
3. Enable **Maps SDK for Android**
4. Create an API key under **Credentials**
5. Open `app/src/main/AndroidManifest.xml`
6. Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual key

### Step 3: Run the App
1. Connect a physical device or start an emulator (API 28+)
2. Click the green **Run** ▶ button
3. Select your device/emulator

---

## Project Architecture (MVVM)

```
┌─────────────────────────────────────────────────────┐
│  UI Layer (Jetpack Compose)                         │
│  SplashScreen, DashboardScreen, AddPlantScreen,     │
│  PlantDetailScreen, StatusUpdateScreen, MapScreen,  │
│  SpeciesGuideScreen, PlantListScreen                │
└─────────────────────┬───────────────────────────────┘
                      │ StateFlow
┌─────────────────────▼───────────────────────────────┐
│  ViewModel Layer                                    │
│  PlantViewModel, StatusViewModel,                   │
│  DashboardViewModel, SpeciesViewModel               │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  Repository Layer                                   │
│  PlantRepository, StatusRepository                  │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  Data Layer (Room Database)                         │
│  PlantEntity, StatusEntity, PlantDao, StatusDao,    │
│  AppDatabase                                        │
└─────────────────────────────────────────────────────┘
```

## File Structure

```
NammaHasiru/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/nammahasiru/app/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── database/
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   ├── PlantDao.kt
│       │   │   │   ├── PlantEntity.kt
│       │   │   │   ├── StatusDao.kt
│       │   │   │   └── StatusEntity.kt
│       │   │   └── repository/
│       │   │       ├── PlantRepository.kt
│       │   │       └── StatusRepository.kt
│       │   ├── ui/
│       │   │   ├── navigation/
│       │   │   │   └── NavGraph.kt
│       │   │   ├── screens/
│       │   │   │   ├── AddPlantScreen.kt
│       │   │   │   ├── DashboardScreen.kt
│       │   │   │   ├── MapScreen.kt
│       │   │   │   ├── PlantDetailScreen.kt
│       │   │   │   ├── PlantListScreen.kt
│       │   │   │   ├── SpeciesGuideScreen.kt
│       │   │   │   ├── SplashScreen.kt
│       │   │   │   └── StatusUpdateScreen.kt
│       │   │   └── theme/
│       │   │       ├── Color.kt
│       │   │       ├── Theme.kt
│       │   │       └── Type.kt
│       │   ├── viewmodel/
│       │   │   ├── DashboardViewModel.kt
│       │   │   ├── PlantViewModel.kt
│       │   │   ├── SpeciesViewModel.kt
│       │   │   └── StatusViewModel.kt
│       │   └── worker/
│       │       └── PlantReminderWorker.kt
│       └── res/
│           ├── drawable/
│           ├── mipmap-anydpi-v26/
│           ├── values/
│           └── xml/
├── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/gradle-wrapper.properties
└── settings.gradle.kts
```

## Key Features Implemented

| Feature | SRD Requirement | Implementation |
|---------|----------------|----------------|
| Geo-tagged plant registration | FR-NH-01, FR-NH-02, FR-NH-03 | AddPlantScreen + CameraX + FusedLocation |
| 90-day reminder | FR-NH-06, FR-NH-07 | WorkManager + NotificationManager |
| Status update with growth photo | FR-NH-08, FR-NH-09 | StatusUpdateScreen + StatusViewModel |
| Community tree map | FR-NH-12 | MapScreen + Google Maps SDK |
| Survival score dashboard | FR-NH-10, FR-NH-11 | DashboardScreen + DashboardViewModel |
| Species success guide | FR-NH-13 | SpeciesGuideScreen + SpeciesViewModel |
| Room DB persistence | F-07 | PlantEntity, StatusEntity + DAOs |

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM
- **Database:** Room (SQLite)
- **Background Tasks:** WorkManager
- **Camera:** CameraX
- **Maps:** Google Maps SDK + Maps Compose
- **Image Loading:** Coil
- **Navigation:** Navigation Compose
- **Location:** FusedLocationProviderClient

## Important Notes

1. **Min SDK:** API 28 (Android 9.0) as per NFR-PORT-01
2. **Permissions:** Only CAMERA + LOCATION + POST_NOTIFICATIONS
3. **No DAO calls in Composables** — All data flows through ViewModels (NFR-MAINT-01)
4. **Offline-first:** All data stored locally in Room DB
5. **Google Maps API Key** must be configured before the Map screen will work

## Troubleshooting

- **Gradle sync fails:** Ensure you have Android SDK 34 installed
- **Map not showing:** Check that your API key is valid and Maps SDK is enabled
- **Camera not working on emulator:** Use a physical device or configure camera in AVD settings
- **Build errors about mipmap:** The project uses adaptive icons — if you get errors, generate proper launcher icons via Android Studio's Image Asset tool (right-click `res` → New → Image Asset)
