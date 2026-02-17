This is a Compose Multiplatform project targeting Android, iOS, Desktop (JVM), Server.

## Description

This repository is based on the course **Multi-Modular Ecommerce App for Android & iOS**  
https://www.udemy.com/course/multi-modular-ecommerce-app-for-android-ios-kmp/

As part of this project, I am learning how to work with **Compose Multiplatform**, as well as
implementing individual solutions based on my own ideas, rather than completely replicating the
implementation from the course.

The project will have its own server implemented on **Ktor**. Unlike the course, which uses
**Firebase**, a separate server solution is used here.

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