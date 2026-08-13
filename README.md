This is a Compose Multiplatform project targeting Android, iOS, Desktop (JVM), Server.

## Description

This is a modular **Kotlin Multiplatform** e-commerce project built with **Compose Multiplatform**
and targeting **Android**, **iOS**, **Desktop (JVM)**, and **Server**.

The codebase is organized around shared business logic, feature modules, core modules, dependency
injection, and store-specific branding. Two store variants — **AthleticaPlus** and
**NutriSport** — reuse the same application logic while providing their own theme and resources.

The backend is implemented with **Ktor** and is being developed as a dedicated server-side
solution for customer-related operations, replacing reliance on Firebase-only backend behavior.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run
widget in your IDE’s toolbar or build it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :stores:athletica-plus
  ./gradlew :stores:nutri-sport
  ```
- on Windows
  ```shell
  .\gradlew.bat :stores:athletica-plus
  .\gradlew.bat :stores:nutri-sport
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run
widget in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :stores:athletica-plus:run
  ./gradlew :stores:nutri-sport:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :stores:athletica-plus:run
  .\gradlew.bat :stores:nutri-sport:run
  ```

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

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run
widget in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from
there.

---