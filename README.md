This is a Compose Multiplatform project targeting Android, iOS, Desktop (JVM), Server.

## Description

This is a modular **Kotlin Multiplatform** e-commerce project built with **Compose Multiplatform**
and targeting **Android**, **iOS**, **Desktop (JVM)**, and **Server**.

The codebase is organized around shared business logic, feature modules, core modules, dependency
injection, and store-specific branding. Two store variants — **AthleticaPlus** and
**NutriSport** — reuse the same application logic while providing their own theme and resources.

The backend is implemented with **Ktor** and is being developed as a dedicated server-side
solution for customer-related operations, replacing reliance on Firebase-only backend behavior.

### Project Layout

The project follows the JetBrains default structure for Kotlin Multiplatform projects with a server:

```
app/
  shared/            # code shared by every client app (App, AppViewModel, entry-point bases)
  athletica-plus/    # store library (theme, strings, iOS framework)
    androidApp/      # Android application
    desktopApp/      # Desktop (JVM) application
  nutri-sport/       # same shape
  iosApp/            # Xcode project with one target per store
core/ component/ feature/ di/ test/   # client libraries
shared/              # client ↔ server contract
server/              # Ktor backend
```

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run
widget in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :app:athletica-plus:androidApp:installDebug
  ./gradlew :app:nutri-sport:androidApp:installDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :app:athletica-plus:androidApp:installDebug
  .\gradlew.bat :app:nutri-sport:androidApp:installDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run
widget in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :app:athletica-plus:desktopApp:run
  ./gradlew :app:nutri-sport:desktopApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :app:athletica-plus:desktopApp:run
  .\gradlew.bat :app:nutri-sport:desktopApp:run
  ```

The desktop app needs `DESKTOP_CLIENT_SECRET` (Google OAuth Desktop client) either as an environment
variable or in a `secrets.properties` file at the repository root.

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run
widget in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

When a desktop app runs at the same time, prefer `:server:runFatJar`.

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run
widget in your IDE’s toolbar or open the [app/iosApp](./app/iosApp) directory in Xcode and run it
from there.

---
