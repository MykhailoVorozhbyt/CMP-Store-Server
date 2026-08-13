# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin Multiplatform (KMP) e-commerce application with a Ktor backend, targeting Android, iOS, Desktop (JVM), and Server. 
Two store variants (AthleticaPlus, NutriSport) share all logic but have their own branding.

The Ktor server owns identity and persistence (SQLite + opaque session tokens). Firebase is **not** the source of truth anymore — it survives only as the mobile Google Sign-In provider (via KMP-Auth) and as a `currentUserId` fallback in `FirebaseAuthSessionDataSource`.

## Build & Run Commands

All commands use the Gradle wrapper. On Windows use `.\gradlew.bat` instead of `./gradlew`. JVM toolchain is **Java 21**.

```bash
# Ktor server (port 8080)
./gradlew :server:run
./gradlew :server:runFatJar        # preferred while a desktop client is also running

# Desktop (JVM) apps
./gradlew :stores:athletica-plus:run
./gradlew :stores:nutri-sport:run

# Android APKs — NOTE: the store modules are KMP libraries; the apps live in :androidApp
# Athletica plus
./gradlew :stores:athletica-plus:androidApp:assembleDebug
./gradlew :stores:athletica-plus:androidApp:assembleRelease -PminifyWithR8=false
# Nutri sport
./gradlew :stores:nutri-sport:androidApp:installDebug

# Tests
./gradlew test                      # everything
./gradlew :server:test
./gradlew :feature:authentication:presentation:jvmTest      # incl. headless Compose UI tests
./gradlew :feature:authentication:presentation:androidDeviceTest  # same UI tests on emulator
```

For iOS: open `/iosApp` in Xcode and run from there.

**Gotcha:** running a desktop store app rebuilds `shared-jvm.jar`, which clobbers the jar a live `:server:run` has loaded → `ClassNotFoundException`, usually surfacing as a `400 SERIALIZATION` response. Run the server via `runFatJar` or `installDist` when working on both sides at once.

## Module Structure

25 Gradle modules, all declared in `settings.gradle.kts` (type-safe project accessors enabled).

```
composeApp/          # Shared Compose entry points: App.kt, StoreApp.kt (Android Application),
                     #   main.kt (desktopApp), MainViewController.kt (iOS), AppViewModel
server/              # Ktor server — see "Server Architecture" below
shared/              # Pure KMP domain: Customer, CartItem, Product, AuthProvider/AuthRequest/AuthResponse,
                     #   NetworkError, Platform, Constants (SERVER_PORT = 8080)
stores/
  athletica-plus/    # KMP library: theme (colors/strings) + iOS/JVM entry points
    androidApp/      # Pure Android application module (applicationId, google-services.json, R8)
  nutri-sport/       # same shape
core/
  domain/            # ApiResult<D,E>, FieldKey, validation contracts
  data/              # EMPTY placeholder module
  presentation/      # BaseActionHandleViewModel, StoreTheme, UiEvent, ViewAction, Screen (nav keys),
                     #   Store* composables, validators, UiText, AppDispatchers
  navigation/        # AppNavigator, RootNavigator, NavigationState, SetupNavGraph, navEntry Koin DSL
  network/           # HttpClientFactory (Ktor client + bearer refresh), SERVER_BASE_URL, safeApiCall
  security/          # SecureStorage expect/actual (Keystore / Keychain / AES-GCM file vault)
  utils/             # Logger (expect/actual), Alpha, runCatchingCancellable
  resources/         # Shared Compose resources (drawables + strings)
feature/
  authentication/
    data/            # KtorAuthDataSource, KtorCustomerDataSource, Default*Repository, DTOs + mappers,
                     #   JvmGoogleSignInService (desktop PKCE OAuth)
    domain/          # AuthRepository/CustomerRepository/GoogleSignInService interfaces + 5 use cases
    presentation/    # AuthenticationScreen/ViewModel, SocialMediaBlock, validators, uiTest/
  home/
    data/            # KtorProductDataSource
    domain/          # ProductDataSource interface + 5 read use cases (NO repository layer here)
    presentation/    # HomeGraphScreen/ViewModel, BottomBar, CustomDrawer, NavigationPlaceholderScreen
di/                  # initializeKoin + module assembly (see "Dependency Injection")
test/                # Shared test fixtures in commonMain — BaseViewModelTest, fakes
build-logic/         # All Gradle convention plugins — never configure modules manually
gradle/              # libs.versions.toml (single source of truth for all versions)
```

## Convention Plugins (build-logic)

**Never configure a module's `build.gradle.kts` manually.** Every module's build file contains only a `plugins { alias(...) }` block. Adding a new module requires:
1. Create the plugin class in `build-logic/convention/src/main/kotlin/plugins/`
2. Register it in `build-logic/convention/build.gradle.kts`
3. Add the plugin ID to `gradle/libs.versions.toml` under `[plugins]`
4. Add entries to `ModulePath` (Gradle path) and `ModuleName` (Android namespace)
5. Apply the alias in the module's `build.gradle.kts` and include it in `settings.gradle.kts`

Inside plugin classes, use the enums in `build-logic/convention/src/main/kotlin/utils/enums/` — never raw strings:
- `ModuleName` — Android namespace strings
- `ModulePath` — Gradle module paths (e.g. `":feature:authentication:domain"`). Note: the two `:androidApp` submodules have no entries.
- `BuildTypeName` — `debug` / `release`

Dependency aliases and plugin IDs come from the version catalog through the accessors in `extensions/ProjectExtensions.kt` (`libs.*`, `pluginManager.alias(libs.plugins.*)`) — there is no `LibraryName` or `PluginName` enum.

Shared helpers live alongside the plugins:
- `configuration/AndroidBase.kt` — `configureAndroidLibraryBase()` (SDK levels, host/device test trees)
- `configuration/IOS.kt` — `configureIOS()` → iosArm64 + iosSimulatorArm64 static framework
- `configuration/ComposeDesktopApplication.kt` — Dmg/Msi/Deb packaging
- `extensions/DependencyExtensions.kt` — `module(ModulePath.X)`, `implementation`, `testImplementation`
- `extensions/SourceSetExtensions.kt` — `sourceSet(name, srcDir)` (used for the shared `uiTest` tree)
- `utils/Java.kt` — Java 21 / `JvmTarget.JVM_21`

## BaseActionHandleViewModel Pattern

All ViewModels extend `BaseActionHandleViewModel<VD>` where `VD` is the ViewData (UI state holder). Constructor takes `dispatchers: AppDispatchers`.

```
User interaction
    → onViewAction(ViewAction)          // called from UI
    → Channel (throttled: 300ms same action dropped)
    → handleViewAction(action)          // override this — single dispatch point
    → emitEvent(UiEvent)                // send events to UI
    → uiEvents SharedFlow               // throttled: 400ms same event, 4000ms same ShowMessage
    → collectEventsWithDefaultProcessing()  // collected in Composable
```

- State: `_viewData: MutableStateFlow<VD>` → exposed as `viewDataState: StateFlow<VD>`
- Update state only via `updateViewData { ... }` (or `_viewData.update { ... }`)
- Each feature defines its own `sealed interface XxxViewAction : ViewAction`
- `ViewAction` and `UiEvent` subclasses must have proper `equals`/`hashCode` (use data class/object)
- Don't block `handleViewAction` — it runs on main thread. Use `launchIo { }` for heavy work.
- Common events live in `core/presentation/.../ui/base/UiEvent.kt`: `ShowMessage`, `HideKeyboard`, `ShowKeyboard`, `ClearFocus`, `Navigate(Screen)`, `NavigateInclusive(Screen)`

Behaviour is pinned by `core/presentation/src/commonTest/.../BaseActionHandleViewModelTest.kt` — update it when you touch the base class.

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

`initializeKoin(vararg appModules, config)` in `:di` (`KoinInit.kt`) starts Koin with `sharedAppModule + platformModule + appModules`.

```
sharedAppModule
├── coreModule            # platformModule (expect/actual, currently empty) +
│                         #   dispatchersModule + networkModule + secureStorageModule
├── authFeatureModule     # authenticationDataModule + authenticationPresentationModule
├── homeFeatureModule     # homeDataModule + homePresentationModule
└── appFeatureModule      # appNavigationModule (placeholder nav entries)
```

**Feature modules own their own Koin modules.** A new feature adds its `xxxDataModule` / `xxxPresentationModule` next to its code and includes them from a `xxxFeatureModule` in `:di` — do not add feature bindings to `coreModule`.

- `dispatchersModule` — a single `AppDispatchers(main, io, default, unconfined)`. Inject that one object; there are **no** `@IoDispatcher`-style annotation qualifiers.
- `networkModule` — `singleOf(::createHttpClient)`, which takes an `AuthSessionDataSource` for the bearer refresh hook.
- `secureStorageModule` — expect/actual; Android binds `AndroidSecureStorage` (needs `androidContext()`), iOS `IosSecureStorage`, JVM `JvmSecureStorage(File(user.home, ".store_app"))`.

`SERVER_BASE_URL` is expect/actual in `core/network/.../ServerUrl.kt`. **The Android actual is a hardcoded LAN IP (`http://192.168.0.118:8080`)** — change `ServerUrl.android.kt` when the dev machine's IP changes, or switch it to `10.0.2.2` for the emulator.

## Navigation (Navigation3)

Uses Jetpack Navigation3 (multiplatform). `Screen` is a `sealed interface Screen : NavKey` in `core/presentation/.../navigation/Screen.kt` (not in `:shared`).

Assembly, all in `:core:navigation`:
- `di/NavModuleExt.kt` — the `navEntry(serializer) { key -> Composable }` and `navKey(serializer)` Koin DSL. Registering an entry also registers a `NavKeyProviderInstaller`.
- `di/RememberNavBackStack.kt` — `rememberKoinNavBackStack` builds a polymorphic `SavedStateConfiguration` from every registered installer. This is why each screen must be registered with its own `serializer()` — KMP has no reflective polymorphic serialization.
- `NavigationState` / `RootNavigator` — a top-level stack plus per-tab sub-stacks; reselecting a tab clears its sub-stack.
- `AppNavigator` (`navigate` / `replaceAll` / `back`), reachable in Composables via `LocalAppNavigator`.
- `NavGraph.kt` — `SetupNavGraph(startDestination)` → `NavDisplay` with `koinEntryProvider()`.

**Adding a screen:** add the object/data class to `Screen.kt`, then `navEntry(Screen.Foo.serializer()) { FooScreen() }` inside the owning feature's presentation Koin module.

Current wiring: `Screen.Auth` and `Screen.HomeGraph` are real screens. Nine more (`ProductsOverview`, `Cart`, `Categories`, `CategorySearch`, `Profile`, `AdminPanel`, `ContactUs`, `Details`, `Checkout`) are registered as `NavigationPlaceholderScreen` in `di/modules/AppNavigationModule.kt`. `ManageProduct` and `PaymentCompleted` are **not registered at all** and will fail at runtime if navigated to.

## Server Architecture

Ktor + Netty on port 8080, entry point `org.cmp.store.ApplicationKt`.

**Layout** — `server/src/main/kotlin/org/cmp/store/`:
```
Application.kt          # main() + Application.module(koinPlugin)
plugins/                # one installXxx() per Ktor plugin
database/tables/        # Exposed table definitions
database/dao/           # Dao interface + DaoImpl pairs (Koin singles)
features/<domain>/      # routes/ + service/ + dto/ + mappers/
di/                     # daoModule, serviceModule
utils/                  # ApiException, DbExtensions, RoutResources, RateLimitExtensions
```

**Plugin install order in `Application.module()`** — order matters:
`DatabaseFactory.init` → `installKoin(koinPlugin)` → `installSerialization` → `installForwardedHeaders` → `installRateLimit` → `installStatusPages` → `installAuthentication` → `install(Resources)` → `installSessionCleanup` → `configureRouting`.
`module()` takes the Koin plugin as a **parameter** (default `Koin`) so tests can pass `KoinIsolated`. There is no CORS or CallLogging installed.

**Persistence** — Exposed v1 JDBC (`org.jetbrains.exposed.v1.jdbc`) over SQLite (`jdbc:sqlite:./store.db`). `DatabaseFactory.init` runs `SchemaUtils.create` on boot, and drops first when `db.recreateSchema=true`. Tables: `customers`, `phone_numbers`, `cart_items`, `products`, `auth_credentials`, `auth_sessions`, `refresh_tokens`.

- Wrap DB access in `dbQuery { }` (`utils/DbExtensions.kt`) — it is `withContext(Dispatchers.IO) { suspendTransaction { ... } }`.
- DAO methods named `*WithinTransaction` are **non-suspend** and must be called inside an existing `dbQuery` — that is how multi-write operations stay atomic. Follow this naming when adding DAO methods.
- `CustomerDaoImpl.mapFrom` deliberately never writes `is_admin`, so admin cannot be granted through the API. Keep it that way.

**Model layering:** Exposed `Table` → shared domain model (`org.cmp.store.domain.*` in `:shared`) → `@Serializable` DTO in `features/<domain>/dto/`. Exception: product routes serialize the domain `Product` directly. Session DTOs are internal row models and never leave the server.

**Routes** (typed `@Resource` classes in `utils/RoutResources.kt`, registered in `plugins/Routing.kt`):

| Method | Path | Auth | Rate limit |
|---|---|---|---|
| GET | `/` | — | global |
| POST | `/auth/authorize` | — | AUTH, key (IP, sha256 email) |
| POST | `/auth/refresh` | — | AUTH, key (IP) |
| POST | `/auth/logout` | bearer | AUTH, key (IP) |
| GET | `/customer/{id}` | bearer + own-customer | global |
| PUT | `/customer` | bearer + own-customer | global |
| GET | `/product/discounted`, `/new`, `/by-ids`, `/by-category/{id}`, `/{id}` | — | global |

`/auth/authorize` is sign-in-**or**-register in one call; the response's `isNewAccount` tells the client which happened.

**Auth** — opaque, DB-backed bearer tokens; **not JWT** (the `jwt.realm` config key only supplies the realm string).
- Passwords: PBKDF2WithHmacSHA256, 120 000 iterations, per-user 16-byte salt, stored `iterations:salt:hash`, constant-time verify.
- Access token 1 h (stored raw as the `auth_sessions` PK); refresh token 7 d sliding (stored SHA-256 hashed only).
- Rotation is family-based. `decideRefreshOutcome` order: expired → reject; unrevoked → rotate; revoked with no successor (deliberate logout) → reject; revoked within a 10 s grace window → rotate (client refresh race); revoked longer ago → **revoke the whole family** (reuse detected → 401 `TOKEN_REUSE_DETECTED`).
- Family revocation commits inside the transaction and throws *after*, which is why the transaction block returns a `RefreshResult` instead of throwing inline.
- Logout revokes only the calling token's family — other devices stay signed in.
- `SessionCleanupJob` sweeps expired rows hourly.

**Errors** — throw `ApiException(status, NetworkError.X)`. `StatusPages` renders the plain enum **name** as the body; the client turns it back into a `NetworkError` in `core/network/utils/SafeApiCall.kt`. Adding a server error code means adding it to `shared/.../network/NetworkError.kt` too.

**Rate limiting** — a bucket is keyed by `(provider name, requestKey)`, **not by route**. Reusing a `RateLimitName` across routes means they share one quota. Global: 120/60 s per IP. `AUTH`: 5/60 s.

**Config** — `server/src/main/resources/application.conf`: port, host, `behindProxy` (`${?BEHIND_PROXY}`), `db.url` / `db.driver` / `db.recreateSchema`, `jwt.realm`. No secrets are consumed by the server (`secrets.properties` at the repo root is Android/desktop-client-side).

**Tests** — `utils/ServerRouteTestSupport.kt` provides `testServerApplication { }` (full app on a fresh per-test SQLite file, `KoinIsolated`) and `testDaoDatabase { }`, plus `seedCustomer`, `grantAdmin`, `customerFixture`. Use these instead of hand-rolling `testApplication`.

## Client Auth & Networking

- **Manual sign-in:** `AuthenticationViewAction.OnSignInClick` → `SignInUseCase(email, password)` → `AuthRepository.authorize` → POST `auth/authorize`. There is no separate registration screen.
- **Google, mobile:** KMP-Auth `GoogleButtonUiContainerFirebase` (via the `expect fun PlatformGoogleButton`) → `FirebaseUser` → `SignInUseCase(AuthUserRequest)` → `authorize(provider = GOOGLE)`.
- **Google, desktop:** `JvmGoogleSignInService` — full OAuth 2.0 Authorization Code + PKCE (S256) with `state`/`nonce`, system browser, `LocalOAuthCallbackServer` on localhost, 180 s timeout. The client secret is resolved at runtime by `DesktopClientSecretResolver` (env `DESKTOP_CLIENT_SECRET` → repo-root `secrets.properties`) and is **never compiled in**.
- **Token storage:** custom `SecureStorage` per platform (AndroidKeyStore AES/GCM + SharedPreferences; iOS Keychain; JVM AES/GCM file vault under `~/.store_app`). Not DataStore, not multiplatform-settings. Wrapped by `SecureStorageAuthSessionDataSource` (mutex-guarded, lazily hydrated cache).
- **Transparent refresh:** Ktor `Auth` plugin `bearer { loadTokens / refreshTokens }` in `core/network/HttpClientFactory.kt`. The refresh call sets `AuthCircuitBreaker` to avoid recursion; on failure it signs out locally and returns null.
- **Errors:** wrap client calls in `safeApiCall { }` → `ApiResult<D, NetworkError>` with `onSuccess` / `onError` / `mapSuccess` from `:core:domain`.

## Testing

`:test` is a fixture module whose sources live in **`commonMain`** (not a test source set), so other modules consume it as a normal dependency via `module(ModulePath.TEST)` in their `commonTest`. It provides `BaseViewModelTest` / `runVmTest` (a `StandardTestDispatcher` wired into `AppDispatchers` plus `Dispatchers.setMain`) and the fakes (`FakeAuthRepository` with a `gate`, `FakeCustomerRepository`, `FakeAuthSessionDataSource`). Its convention plugin re-exports kotlin-test, coroutines-test, Turbine, koin-test and compose-ui-test as `api`.

Compose UI tests live in `feature/authentication/presentation/src/uiTest/` and are wired by `kotlin.srcDir` into **both** `jvmTest` (headless) and `androidDeviceTest` (emulator) — not via `dependsOn`, which is forbidden across source-set trees.

Covered today: `BaseActionHandleViewModel`, string utils, `HttpClientFactory` + `SessionRefreshCall`, both auth repositories and all four Ktor data sources, auth use cases, `AuthenticationViewModel`, the desktop OAuth stack, and the whole server (~150 tests). Untested: `core:security`, `core:navigation`, `core:utils`, `core:domain`, `di`, `feature:home:presentation`.

## Key Versions

`gradle/libs.versions.toml` is the single source of truth. Highlights: Kotlin 2.3.21, AGP 9.2.1, KSP 2.3.9, compileSdk 37 / minSdk 24 / targetSdk 36, Java 21, Compose Multiplatform 1.11.1 (material3 1.9.0), Ktor 3.5.0 (server + client), Koin 4.2.1, Exposed 1.3.0 + sqlite-jdbc 3.53.2.0, Navigation3 UI 1.1.1, coroutines 1.11.0, kmpauth 2.5.0-alpha01, Firebase BOM 34.14.1, Turbine 1.2.1.

## Known Gaps (as of 2026-08-07)

- `:core:data` is registered but contains no sources.
- `Screen.ManageProduct` and `Screen.PaymentCompleted` have no `navEntry` — navigating to them fails.
- `Screen.ContactUs`'s placeholder is mislabelled `"Products overview"` in `di/modules/AppNavigationModule.kt`.
- Server has no CORS and no CallLogging; SQLite is a single dev file with no migrations (schema is recreated, not migrated).
- A future authed "set password" endpoint (letting Google users add a MANUAL credential) is designed but not built — it needs a profile screen first.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).
