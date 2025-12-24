# Alphabeto

A fun and educational Android app designed to help children learn alphabets in multiple languages
through interactive letter tracing and audio pronunciation.

## Features

- **Multi-Language Support**: Learn the Arabic and French alphabets
- **Interactive Letter Tracing**: Practice writing letters with touch-based tracing
- **Audio Pronunciation**: Hear the correct pronunciation for each letter
- **Progress Tracking**: Track learning progress for each letter
- **Kid-Friendly Interface**: Colorful, intuitive design optimized for young learners
- **Tablet Support**: Adaptive grid layouts for phones and tablets

## Screenshots

<!-- Add your screenshots here -->

## Tech Stack

- **Language**: Kotlin
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 36
- **Architecture**: MVVM with Clean Architecture
- **UI**: View Binding, Material Design 3
- **Navigation**: Jetpack Navigation Component
- **Database**: Room for local data persistence
- **Async**: Kotlin Coroutines & Flow
- **Dependency Injection**: Service Locator pattern

## Project Structure

```
app/src/main/java/com/alphabeto/
├── data/
│   ├── local/          # Room database & DAOs
│   ├── model/          # Data models (Letter, etc.)
│   └── repository/     # Data repositories
├── di/                 # Dependency injection (ServiceLocator)
├── domain/
│   └── usecase/        # Business logic use cases
├── ui/
│   ├── arabic/         # Arabic alphabet screen
│   ├── common/         # Shared UI components
│   ├── french/         # French alphabet screen
│   └── main/           # Main menu
└── utils/              # Utilities (SoundManager, etc.)
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK with API level 26+

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Alphabeto.git
   ```

2. Open the project in Android Studio

3. Sync Gradle and wait for dependencies to download

4. Run the app on an emulator or physical device:
   ```bash
   ./gradlew installDebug
   ```

## Building

### Debug Build

```bash
./gradlew assembleDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

## Usage

1. **Launch the app** and choose a language (Arabic or French) from the main menu
2. **Browse letters** in the alphabet grid - each letter card shows the character
3. **Tap a letter** to hear its pronunciation and start tracing practice
4. **Trace the letter** on screen following the guide
5. **Track progress** - completed letters are marked in the grid
6. **Reset progress** anytime via the menu option

## Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| AndroidX Core KTX | 1.17.0 | Kotlin extensions for Android |
| Material Design | 1.13.0 | UI components |
| Room | 2.6.1 | Local database |
| Navigation | 2.9.6 | Fragment navigation |
| Lifecycle | 2.8.7 | ViewModel & LiveData |
| Coroutines | 1.9.0 | Async operations |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Sound assets for letter pronunciations
- Material Design icons and components
- The Android developer community

---

Made with love for little learners
