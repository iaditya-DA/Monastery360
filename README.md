
# Monastery360

Monastery360 is an immersive Android application that provides detailed information, history, and interactive experiences related to Buddhist monasteries, with a focus on Sikkim and the Himalayan region. The app features a modern UI, multi-language support, and integrates advanced features like 3D models, weather, and digital archives.

---

## Table of Contents
- [Features](#features)
- [Folder Structure Explained](#folder-structure-explained)
- [Getting Started](#getting-started)
- [Assets & Resources](#assets--resources)
- [Contributing](#contributing)
- [License](#license)

---

## Features
- **Monastery Explorer:** Browse a curated list of monasteries with detailed descriptions, history, architecture, and images.
- **3D Model Viewer:** View 3D models of select monasteries (e.g., Rumtek Monastery).
- **Weather Integration:** Get real-time weather updates for monastery locations.
- **Digital Manuscript Archive:** Access digitized manuscripts and their translations.
- **Multi-language Support:** Available in English, Bengali, Hindi, Nepali, and more.
- **Interactive Maps:** Locate monasteries and attractions on integrated maps.
- **Favorites & Planning:** Mark favorites and plan itineraries for monastery visits.
- **Camera & AR:** Capture photos and explore AR features (if supported).
- **Modern UI:** Custom themes, animations, and responsive layouts.

---

## Folder Structure Explained

```
Monastery360/
│
├── app/                        # Main Android app module
│   ├── assets/                 # Static assets (e.g., monasteries.json)
│   ├── build.gradle.kts        # Module-level Gradle build script
│   ├── proguard-rules.pro      # ProGuard rules for release builds
│   ├── google-services.json    # Firebase/Google services config
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml         # App manifest
│       │   ├── java/com/example/monastery360/
│       │   │   ├── adapters/               # RecyclerView & UI adapters
│       │   │   ├── database/               # Room DB entities, DAOs, repositories
│       │   │   ├── manager/                # Managers (e.g., FavoritesManager)
│       │   │   ├── model/                  # Data models (Monastery, Manuscript, etc.)
│       │   │   ├── repository/             # Data repositories
│       │   │   ├── utils/                  # Utility classes (e.g., LocaleHelper)
│       │   │   ├── viewmodel/              # ViewModels for MVVM architecture
│       │   │   ├── ...Activity.kt          # UI screens (activities/fragments)
│       │   ├── res/
│       │   │   ├── layout/                 # XML UI layouts (screens, items, dialogs)
│       │   │   ├── drawable/               # Images, icons, vector drawables
│       │   │   ├── values/                 # Strings, colors, dimensions, styles
│       │   │   ├── values-bn/              # Bengali translations
│       │   │   ├── values-hi/              # Hindi translations
│       │   │   ├── values-ne/              # Nepali translations
│       │   │   ├── values-night/           # Night mode themes
│       │   │   ├── values-sw600dp/         # Tablet-specific dimensions
│       │   │   ├── raw/                    # Raw assets (e.g., 3D models)
│       │   │   ├── xml/                    # XML configs (e.g., backup, file paths)
│       ├── test/                           # Unit tests
│       └── androidTest/                    # Instrumented tests
│
├── gradle/                     # Gradle wrapper and version catalogs
│   ├── libs.versions.toml      # Library versions
│   └── wrapper/                # Gradle wrapper files
│
├── build.gradle.kts            # Project-level Gradle build script
├── gradle.properties           # Gradle properties
├── settings.gradle.kts         # Gradle settings
├── README.md                   # Project documentation
└── ...                         # Other project files
```

### Key Folders
- **adapters/**: UI adapters for RecyclerViews and lists.
- **database/**: Room database setup (entities, DAOs, repositories).
- **manager/**: App-wide managers (e.g., favorites).
- **model/**: Data models for monasteries, manuscripts, posts, etc.
- **repository/**: Data access and business logic.
- **utils/**: Helper utilities (e.g., language switching).
- **viewmodel/**: ViewModels for MVVM pattern.
- **layout/**: All UI XML layouts for screens and components.
- **drawable/**: All images, icons, and vector assets.
- **values/**: App-wide resources (strings, colors, styles, dimensions).
- **values-*/**: Translations and device-specific resources.
- **raw/**: 3D models and other raw assets.
- **xml/**: Configuration XMLs (e.g., backup, file paths).

---

## Getting Started

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (latest recommended)
- JDK 17 or newer
- Gradle (wrapper included)

### Build & Run
1. Clone the repository:
   ```sh
   git clone https://github.com/iaditya-DA/Monastery360.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and build the project.
4. Run the app on an emulator or physical device.

### Gradle Commands
- Build the project:
  ```sh
  ./gradlew build
  ```
- Clean the project:
  ```sh
  ./gradlew clean
  ```

---

## Assets & Resources
- `assets/monasteries.json`: Contains monastery data used in the app.
- `res/drawable/`: All images, icons, and vector assets.
- `res/layout/`: All UI XML layouts.
- `res/values/`: Strings, colors, styles, and dimensions.
- `res/raw/`: 3D models and other raw assets.
- `res/xml/`: Configuration XMLs.

---

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

**Developed by aditya with 💖**
---

