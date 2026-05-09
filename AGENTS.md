# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## Project Overview

A Kotlin Multiplatform (KMP) e-commerce application with a Ktor backend, targeting Android, iOS, Desktop (JVM), and Server. Two store variants (AthleticaPlus, NutriSport) share all logic but have their own branding. The Ktor server is being built as a replacement for Firebase.

## Build & Run Commands

All commands use Gradle wrapper. On Windows use `.\gradlew.bat` instead of `./gradlew`.

```bash
# Run the Ktor server (port 8080)
./gradlew :server:run

# Run Desktop (JVM) apps
./gradlew :stores:athletica-plus:run
./gradlew :stores:nutri-sport:run

# Build Android APKs
./gradlew :stores:athletica-plus:assembleDebug
./gradlew :stores:nutri-sport:assembleDebug

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :server:test
./gradlew :feature:authentication:domain:test
```

For iOS: open `/iosApp` in Xcode and run from there.

## Module Structure

```
composeApp/          # Shared Compose entry points: App.kt (common), MainActivity (Android), main.kt (JVM), MainViewController (iOS)
server/              # Ktor server — Netty, port 8080; customer CRUD routes + in-memory CustomerStorage
shared/              # KMP domain models (Customer, CartItem), Screen nav keys, Constants, HttpClientFactory
stores/
  athletica-plus/    # Only has theme (colors/strings) + platform entry points
  nutri-sport/       # Only has theme (colors/strings) + platform entry points
feature/
  authentication/
    data/            # CustomerApi (Ktor wrapper), CustomerRepositoryImpl (Firebase Auth + Ktor)
    domain/          # CustomerRepository interface, CreateCustomerUseCase (stub, not implemented)
    presentation/    # AuthenticationScreen, AuthenticationViewModel, SocialMediaBlock (KMP-Auth)
core/
  presentation/      # BaseActionHandleViewModel, StoreTheme, UiEvent, ViewAction, StoreSnackbar
  navigation/        # SetupNavGraph (NavDisplay with Navigation3)
  utils/             # Logger (expect/actual), AdaptivePreview annotation, Alpha
  resources/         # Shared Compose resources
di/                  # Koin modules: networkModule, dispatchersModule, viewModelModule, repositoryModule, targetModule
build-logic/         # All Gradle convention plugins — never configure modules manually
gradle/              # libs.versions.toml (single source of truth for all versions)
```

## Convention Plugins (build-logic)

**Never configure a module's `build.gradle.kts` manually.** Every module applies exactly one convention plugin. Adding a new module requires:
1. Create the plugin class in `build-logic/convention/src/main/kotlin/plugins/`
2. Register it in `build-logic/convention/build.gradle.kts`
3. Add the plugin ID to `gradle/libs.versions.toml` under `[plugins]`
4. Apply it in the module's `build.gradle.kts`

Inside plugin classes, use the enum types — never raw strings:
- `LibraryName` — all dependency aliases
- `ModuleName` — Android namespace strings
- `ModulePath` — Gradle module paths (e.g. `":feature:authentication:domain"`)
- `PluginName` — plugin IDs

## BaseActionHandleViewModel Pattern

All ViewModels extend `BaseActionHandleViewModel<VD>` where `VD` is the ViewData (UI state holder).

```
User interaction
    → onViewAction(ViewAction)          // called from UI
    → Channel (throttled: 300ms same action dropped)
    → handleViewAction(action)          // override this — single dispatch point
    → emitEvent(UiEvent)               // send events to UI
    → uiEvents SharedFlow              // throttled: 400ms same event, 4000ms same ShowMessage
    → collectEventsWithDefaultProcessing()  // collected in Composable
```

- State: `_viewData: MutableStateFlow<VD>` → exposed as `viewDataState: StateFlow<VD>`
- Update state only via `_viewData.update { ... }` or `updateViewData { ... }`
- Each feature defines its own `sealed interface XxxViewAction : ViewAction`
- `ViewAction` and `UiEvent` subclasses must have proper `equals`/`hashCode` (use data class/object)
- Don't block `handleViewAction` — it runs on main thread. Use `launchIo { }` for heavy work.

## StoreTheme System

`StoreTheme` is a Compose `object` exposing `color`, `typography`, `dimens`, `strings`, `windowTypography` via `CompositionLocal`.

- **`BaseTheme {}`** — use in production UI. Injects `StoreThemeProvider` and `AppStrings` from Koin.
- **`PreviewTheme {}`** — use in Composable `@Preview` functions only. No Koin, hardcoded values.

Each store provides its own theme via a Koin module:
```kotlin
val athleticaPlusThemeModule = module {
    singleOf(::AthleticaPlusStoreThemeProvider).bind<StoreThemeProvider>()
    singleOf(::AthleticaPlusStrings).bind<AppStrings>()
}
```
This module is passed as `appModules` when calling `initializeKoin(...)` from the store's entry point.

## Dependency Injection (Koin)

`initializeKoin(config, vararg appModules)` in `:di` starts Koin with:
- `targetModule` — expect/actual, platform-specific extras
- `networkModule` — `HttpClient` (via `createHttpClient()` in `shared/.../network/HttpClientFactory.kt`) and `CustomerApi`; `SERVER_BASE_URL` is platform-specific (Android emulator: `10.0.2.2:8080`)
- `repositoryModule` — `CustomerRepository` → `CustomerRepositoryImpl`
- `dispatchersModule` — coroutine dispatchers with qualifiers
- `viewModelModule` — `AuthenticationViewModel`
- `*appModules` — store-specific modules (e.g. `athleticaPlusThemeModule`)

Dispatcher injection uses annotation qualifiers: `@IoDispatcher`, `@MainDispatcher`, `@DefaultDispatcher`, `@UnconfinedDispatcher`. Always inject dispatchers this way in ViewModels and use-cases.

## Navigation (Navigation3)

Uses Jetpack Navigation3 (multiplatform alpha). Screens are defined as a `sealed interface Screen : NavKey` in `:shared`. The nav graph is in `:core:navigation/NavGraph.kt`.

Only `Screen.Auth` and `Screen.HomeGraph` are wired in the nav graph. All other screens in `Screen.kt` are defined but not yet registered.

## Current State (as of 2026-04-04)

- Server: `GET /` (greeting), `POST /customer`, `GET /customer/{id}`, `PUT /customer`. Storage is an in-memory `ConcurrentHashMap` (`CustomerStorage`) — temporary, no DB yet.
- Auth: Google Sign-In uses KMP-Auth (`GoogleButtonUiContainerFirebase`). `handleGoogleSignInSuccess()` in `AuthenticationViewModel` still has `createCustomer()` and navigation-to-home commented out.
- `CustomerRepositoryImpl` is implemented (Firebase Auth for identity + Ktor `CustomerApi` for persistence).
- `CreateCustomerUseCase.invoke()` is a stub — not yet implemented.