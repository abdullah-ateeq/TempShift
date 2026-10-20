# TempShift 🌡️

**TempShift** is a modern, fast, and high-precision temperature conversion application for Android built with **Jetpack Compose** and **Material Design 3**. It allows seamless conversion between **Celsius**, **Fahrenheit**, and **Kelvin** with real-time calculations, step-by-step formula explanations, history tracking, customizable settings, and absolute-zero scientific validation.

---

## ✨ Features

- 🔄 **Real-time Unit Conversion**: Instant calculations between Celsius (°C), Fahrenheit (°F), and Kelvin (K).
- 🧮 **High-Precision Math**: Uses `BigDecimal` calculations under the hood to ensure exact results without floating-point rounding errors.
- 📐 **Step-by-Step Formulas**: Live breakdown of the exact formula and step-by-step mathematical steps used for every conversion.
- 🧊 **Absolute Zero Validation**: Built-in physical limit checks enforcing absolute zero boundaries (e.g., $0\text{ K}$, $-273.15^\circ\text{C}$, $-459.67^\circ\text{F}$).
- ⚡ **Quick Reference Values**: One-tap access to common benchmark temperatures like Freezing ($0^\circ\text{C}$), Room Temperature ($20^\circ\text{C}$), Body Temperature ($37^\circ\text{C}$), and Boiling ($100^\circ\text{C}$).
- 📊 **Visual Temperature Scale**: Interactive visual comparison across temperature scales.
- 📜 **Conversion History**: Save, restore, and manage previous conversions locally.
- ⚙️ **Customizable Settings**:
  - Precision control (0 to 6 decimal places).
  - Rounding mode options (Half Up, Half Even, Floor, Ceiling).
  - Theme mode (System, Light, Dark) with Material You Dynamic Color support.
  - Optional Haptic Feedback and UI animations.
  - Option to preserve the last conversion state on app restart.
- 🔒 **100% Offline & Private**: Zero network permissions required, no tracking, and no external dependencies. Works completely offline.

---

## 📏 Conversion Formulas Reference

| Conversion | Formula |
| :--- | :--- |
| **Celsius to Fahrenheit** | $^\circ\text{F} = (^\circ\text{C} \times \frac{9}{5}) + 32$ |
| **Fahrenheit to Celsius** | $^\circ\text{C} = (^\circ\text{F} - 32) \times \frac{5}{9}$ |
| **Celsius to Kelvin** | $\text{K} = ^\circ\text{C} + 273.15$ |
| **Kelvin to Celsius** | $^\circ\text{C} = \text{K} - 273.15$ |
| **Fahrenheit to Kelvin** | $\text{K} = (^\circ\text{F} - 32) \times \frac{5}{9} + 273.15$ |
| **Kelvin to Fahrenheit** | $^\circ\text{F} = (\text{K} - 273.15) \times \frac{9}{5} + 32$ |

### Absolute Zero Limits:
$$\text{Absolute Zero} = -273.15^\circ\text{C} = -459.67^\circ\text{F} = 0\text{ K}$$

---

## 🛠️ Architecture & Tech Stack

TempShift is built using modern Android development practices adhering to **Clean Architecture** and **Unidirectional Data Flow (UDF)**.

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (`androidx.compose.material3`)
- **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture (Data, Domain, UI layers)
- **State Management**: Kotlin `StateFlow` and `SharedFlow` with `ViewModel`
- **Data Persistence**: `SharedPreferences` / `JSON` local storage repositories
- **Build System**: Gradle Version Catalogs (`libs.versions.toml`) with Kotlin DSL
- **Compatibility**: Android 8.0 (API level 26) and higher (Targeting API level 37)

---

## 📁 Project Structure

```text
com.example.tempshift/
├── data/
│   ├── model/         # Data models (TempShiftHistory, ThemeMode)
│   └── repository/    # Local repositories for Settings and History
├── domain/            # Business logic, math engine & validators
│   ├── TemperatureCalculator.kt
│   ├── TemperatureValidator.kt
│   ├── TemperatureFormulaProvider.kt
│   ├── TemperatureExplanationBuilder.kt
│   └── TemperatureUnit.kt
├── ui/
│   ├── about/         # About screen & privacy info
│   ├── history/       # Conversion history screen
│   ├── home/          # Home conversion screen & components
│   │   └── components/# Formula cards, visual scale, quick values
│   ├── navigation/    # Navigation graph & bottom bar setup
│   ├── settings/      # Application preferences & settings screen
│   ├── splash/        # Animated splash screen
│   ├── theme/         # Material 3 colors, typography & shapes
│   ├── TempShiftUiState.kt
│   └── TempShiftViewModel.kt
└── MainActivity.kt    # Main activity entry point
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2.1) or newer recommended.
- **JDK**: Java 11 or higher.
- **Android SDK**: API level 37 installed (Min API: 26).

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/TempShift.git
   cd TempShift
   ```
2. Open the project in **Android Studio**.
3. Let Gradle synchronize dependencies.
4. Select an emulator or physical device running Android 8.0+ (API 26+).
5. Click **Run (Shift + F10)**.

### Running Tests
To execute unit tests:
```bash
./gradlew test
```

---

## 📜 License

```text
Copyright 2026 Abdullah Ateeq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
