# Motion Lab 📐

**Motion Lab** is a minimalist, scientific instrument-inspired Android application built with **Kotlin** and **Jetpack Compose**. It provides real-time visualization and measurement of device motion orientation and ambient light illuminance.

---

## 🌟 Application Overview

Motion Lab transforms Android hardware sensors into an interactive perspective instrument:
- **Device Orientation & Perspective Plane**: A custom Canvas-rendered 3D grid plane that dynamically tilts in real-time according to physical device pitch and roll angles, equipped with a plumb level indicator ball and target reticle.
- **Ambient Light Monitor**: Displays real-time lux readings, a logarithmic intensity meter, and qualitative lighting condition categories (Dark, Dim Light, Indoor, Daylight, Direct Sunlight).
- **Linear Acceleration Tri-Axis Vectors**: Displays calibrated X, Y, and Z acceleration components ($m/s^2$) alongside overall vector magnitude $|g|$.

---

## 📡 Sensors Implemented

| Category | Sensor Type | Fallback Strategy | Purpose & Usage |
| :--- | :--- | :--- | :--- |
| **Motion / Orientation** | `Sensor.TYPE_GRAVITY` | `Sensor.TYPE_ACCELEROMETER` | Primary tilt angles (Pitch & Roll in degrees) and perspective 3D grid transformation. `TYPE_GRAVITY` provides stable isolation from rapid linear acceleration; falls back automatically to `TYPE_ACCELEROMETER` if hardware gravity sensor is unavailable. |
| **Ambient Light** | `Sensor.TYPE_LIGHT` | Graceful Unavailable State | Real-time illuminance in lux ($lx$), logarithmic intensity meter, and qualitative environment status. |

---

## 🎨 How Sensor Readings Influence the UI

1. **3D Perspective Canvas Grid**:
   - Device **Pitch** (front/back tilt) and **Roll** (left/right tilt) update the 3D perspective projection matrix.
   - The green grid plane rotates and tapers realistically toward the horizon as the device tilts.
   - A floating **Plumb Ball / Level Indicator** moves inside the central crosshair target to show surface levelness.
2. **Angle Readout Cards**:
   - Monospaced pitch and roll values ($^\circ$) with floating status badges indicating active sensor (`TYPE_GRAVITY` vs `TYPE_ACCELEROMETER`).
3. **Ambient Light Illuminance Meter**:
   - Live lux values update instantly.
   - A smooth logarithmic bar meter visualizes relative light level against standard thresholds (0 to 10,000+ lx).
   - Qualitative status tags (`INDOOR LIGHT`, `DAYLIGHT`, etc.) adapt dynamically.
4. **Vector Acceleration**:
   - Live X, Y, Z tri-axis acceleration readouts in $m/s^2$ with continuous vector magnitude $|g| = \sqrt{x^2 + y^2 + z^2}$.

---

## 🛠️ Build and Run Instructions

### Prerequisites
- **Android Studio**: Ladybug / Meerkat / 2024.2+ or higher
- **JDK**: Java 17
- **Min SDK**: Android 7.0 (API 24)
- **Target / Compile SDK**: Android API 37

### Build Steps
1. Clone or open the project root directory in Android Studio.
2. Build the application via Gradle command line:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run local unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Install on a connected physical Android device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## ⚡ Lifecycle & Resource Management

- **Lifecycle Listener Management**:
  - Sensor listeners are strictly registered during `onResume()` / `Lifecycle.Event.ON_RESUME` and unregistered during `onPause()` / `Lifecycle.Event.ON_PAUSE`.
  - Prevents battery drain and background memory leaks.
- **Smoothing & Low-Pass Filtering**:
  - Applied Exponential Moving Average ($\alpha = 0.20$) to eliminate raw micro-jitter while preserving low-latency physical feedback.

---

## 📋 Verification & Device Testing Status

- **Automated Unit Tests**:
  - Unit tests in `SensorLogicUnitTest.kt` passed successfully (5/5 tests passed).
- **Build Verification**:
  - `assembleDebug` completed with 0 errors.
- **Device Testing Notice**:
  - Tested on Gradle build system and Android build tools.
  - Hardware testing requires an Android device or Android Emulator with Sensor Injection enabled in Extended Controls.

---

## 📄 License & Student Details
Designed and built for Motion Lab Android Sensor Exploration Assignment.
