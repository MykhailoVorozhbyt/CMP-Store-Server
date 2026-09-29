# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin Multiplatform (KMP) e-commerce application with a Ktor backend, targeting Android, iOS, Desktop (JVM), and Server. 
Two store variants (AthleticaPlus, NutriSport) share all logic but have their own branding.

The Ktor server owns identity and persistence (SQLite + opaque session tokens). 
Firebase is **not** the source of truth anymore — it survives only as the mobile Google Sign-In provider (via KMP-Auth) and as a `currentUserId` fallback in `FirebaseLocalAuthSessionDataSource`.

Client code is split into `component/<entity>/` (business logic) and `feature/<name>` (UI only) — see
"App Architecture". The split is enforced by `./gradlew validateArchitectureDependencies`.

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
# Nutri sport
./gradlew :stores:nutri-sport:androidApp:installDebug

# Tests — same split as CI
./gradlew :server:test
./gradlew allTests -x :server:test  # every KMP module (jvm + androidHost); iOS only on macOS
./gradlew validateArchitectureDependencies  # module-dependency rules; run by NAME (configure-on-demand)
./gradlew :feature:authentication:jvmTest      # incl. headless Compose UI tests
./gradlew :feature:authentication:androidDeviceTest  # same UI tests on emulator
```

For iOS: open `/iosApp` in Xcode and run from there.

**Gotcha:** running a desktop store app rebuilds `shared-jvm.jar`, which clobbers the jar a live `:server:run` has loaded → `ClassNotFoundException`, usually surfacing as a `400 SERIALIZATION` response. Run the server via `runFatJar` or `installDist` when working on both sides at once.

## Module Structure

32 `include`s in `settings.gradle.kts` (type-safe project accessors enabled): 3 components × 4 layers, 2 features,
8 core modules, plus app/server/shared/stores/di/test.

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
  network/           # HttpClientFactory (Ktor client + bearer refresh), SERVER_BASE_URL, safeApiCall,
                     #   NetworkError.toDataError()
  security/          # SecureStorage expect/actual (Keystore / Keychain / AES-GCM file vault) + secureStorageModule,
                     #   LocalAuthSessionDataSource interface (impls live in component/auth/data)
  utils/             # Logger (expect/actual), Alpha, runCatchingCancellable
  resources/         # Shared Compose resources (drawables + strings)
component/
  customer/
    model/           # CustomerError (NotFound | Common(DataError)); Customer comes from :shared
    domain-api/      # CustomerRepository (readCustomer, updateCustomer)
    usecase/         # ReadCustomerUseCase, UpdateCustomerUseCase + customerUseCaseModule
    data/            # internal KtorCustomerDataSource, DefaultCustomerRepository, DTOs, mappers + customerDataModule
  product/
    model/           # ProductError (NotFound | InvalidCategory | Common(DataError)); Product comes from :shared
    domain-api/      # ProductRepository (discounted, new, byId, byIds, byCategory)
    usecase/         # 5 Read*UseCase + productUseCaseModule
    data/            # internal KtorProductDataSource, DefaultProductRepository, error mapper + productDataModule
                     #   (Product itself is the wire model — no DTO, same as on the server)
  auth/
    model/           # AuthError (InvalidCredentials | AccountHasNoPassword | … | Common(DataError)), SignInResult,
                     #   GoogleSignInError, AuthUserRequest, WEB_CLIENT_ID / DESKTOP_CLIENT_ID
    domain-api/      # AuthRepository (authorize, signOut, currentUserId), GoogleSignInService
    usecase/         # SignIn, SignOut, GetCurrentUserId, RequestGoogleAccount + authUseCaseModule
    data/            # KtorAuthDataSource, DefaultAuthRepository, auth DTOs (incl. its own CustomerDto for the
                     #   /auth/authorize response), Default/FirebaseLocalAuthSessionDataSource,
                     #   jvmMain JvmGoogleSignInService (desktop PKCE OAuth) + authenticationDataModule.
                     #   Plugins: store.component.data + store.firebase.auth
feature/             # one module per feature = its presentation; package com.store.feature.<name>,
                     #   plugin store.feature.presentation
  authentication/    # AuthenticationScreen/ViewModel, SocialMediaBlock, validators, uiTest/
                     #   (+ store.feature.uiTest, store.firebase.auth, store.kmpauth.google)
  home/              # HomeGraphScreen/ViewModel, BottomBar, CustomDrawer, NavigationPlaceholderScreen
                     #   (uses component/auth, component/customer, component/product)
di/                  # initializeKoin + module assembly (see "Dependency Injection") + KoinGraphTest (jvmTest)
test/                # Shared test fixtures in commonMain — BaseViewModelTest, fakes, Ktor mock helpers
config/architecture/ # rules.json for validateArchitectureDependencies
docs/reference/      # android-component-architecture.md — inspiration from another (Hilt) project, not rules
build-logic/         # All Gradle convention plugins — never configure modules manually
gradle/              # libs.versions.toml (single source of truth for all versions)
```

## Convention Plugins (build-logic)

**Never configure a module's `build.gradle.kts` manually.** Every module's build file contains only a `plugins { alias(...) }` block.

**Plugin families:**
- **Layer plugins** — `store.component.model` / `.domainApi` / `.usecase` / `.data` and one `store.feature.presentation`.
  They derive the namespace from the path, auto-wire the sibling layers of the same component (via `project.parent`),
  and fail the build if the path is not `:component:<name>:<layer>` / `:feature:<name>` or `<name>` is
  missing from `ComponentName` / `FeatureName`. Component layers get no Compose and no Android resources.
- **Capability plugins** — stacked next to a layer plugin for extras: `store.firebase.auth`, `store.kmpauth.google`,
  `store.feature.uiTest`. They wait for KMP (`withPlugin`), so their order in `plugins { }` doesn't matter.
- **Per-module plugins** for everything else (`core:*`, `:shared`, `composeApp`, `di`, `test`, `server`, stores) and the
  root-only `store.architecture`.

**Adding a component** (e.g. `basket`):
1. Add `BASKET("basket")` to `utils/enums/ComponentName.kt`.
2. `include` `:component:basket:{model,domain-api,usecase,data}` in `settings.gradle.kts`.
3. One-line `build.gradle.kts` per layer: `alias(libs.plugins.store.component.<layer>)`.
4. Code in package `com.store.component.basket.<layer>` (`domain-api` → `domain_api`); `usecase/di` + `data/di` Koin modules.
5. `:di`: `basketComponentModule = module { includes(basketDataModule, basketUseCaseModule) }` in `componentModules`,
   plus `module(component(ComponentName.BASKET, DATA / USECASE))` in `DiModulePlugin`.
6. If it is a *process* component that reads another component's `domain-api`, add it to the `(?!(basket|order|payment):)`
   lookahead in `rules.json`.

**Adding a feature** (e.g. `profile`):
1. Add `PROFILE(dirName = "profile", components = listOf(...))` to `utils/enums/FeatureName.kt` — the plugin wires the
   listed components' `usecase` + `model`.
2. `include(":feature:profile")`, `feature/profile/build.gradle.kts` = `alias(libs.plugins.store.feature.presentation)`
   (+ capability plugins if needed).
3. Package `com.store.feature.profile`; `navEntry(...)` in its presentation Koin module (`di/ProfilePresentationModule.kt`); include that module
   from a `profileFeatureModule` in `:di` `featureModules`; add a `ModulePath` entry for `DiModulePlugin`.

**Enums** in `build-logic/convention/src/main/kotlin/utils/enums/` — never raw strings in plugins:
- `ComponentName`, `ComponentLayer`, `FeatureName` — see above; `component(name, layer)` builds a typed path
  (`module(component(...))` / `apiModule(component(...))`).
- `ModulePath` — Gradle paths of non-component modules. The two `:androidApp` submodules have no entries.
- `ModuleName` — only namespaces that do NOT follow the path rule (`:shared`, `composeApp`, stores, core modules).
  Component and feature namespaces are derived (`extensions/NamespaceExtensions.kt`).
- `BuildTypeName` — `debug` / `release`.

Dependency aliases and plugin IDs come from the version catalog through the accessors in `extensions/ProjectExtensions.kt` (`libs.*`, `pluginManager.alias(libs.plugins.*)`) — there is no `LibraryName` or `PluginName` enum.

Shared helpers live alongside the plugins:
- `configuration/AndroidBase.kt` — `configureAndroidLibraryBase(namespace, enableAndroidResources = true)` (SDK levels, host/device test trees)
- `configuration/PureKmp.kt` — `configurePureKmpLibrary()` for component layers (android + iOS + jvm, no Compose/resources)
- `configuration/IOS.kt` — `configureIOS()` → iosArm64 + iosSimulatorArm64 static framework
- `configuration/ComposeDesktopApplication.kt` — Dmg/Msi/Deb packaging
- `extensions/DependencyExtensions.kt` — `module(ModulePath.X)`, `implementation`, `testImplementation`
- `extensions/ComponentExtensions.kt` — `component()`, `module()/apiModule(ComponentModule)`, `owningComponent()`
- `extensions/SourceSetExtensions.kt` — `sourceSet(name, srcDir)`
- `architecture/` — `ArchitectureRules` + `ValidateArchitectureDependenciesTask` (see "Enforcement")
- `utils/Java.kt` — Java 21 / `JvmTarget.JVM_21`

After editing anything in `build-logic`, sync and run `./gradlew --stop` — the daemon keeps the old plugin classes.

## Code Style

**Default: no comments** — neither KDoc nor plain `//` lines. A comment is justified only when it explains *why*
and the code genuinely cannot express it; that should be rare. If code is unclear without a comment, fix the code:
rename the variable, extract a constant, or pull out a function with a telling name. "What happens here" comments
and KDoc that restates a signature drift away from the code during refactoring and turn into noise.

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
├── componentModules
│   ├── authComponentModule      # authenticationDataModule + authUseCaseModule
│   ├── customerComponentModule  # customerDataModule + customerUseCaseModule
│   └── productComponentModule   # productDataModule + productUseCaseModule
└── featureModules
    ├── authFeatureModule     # authenticationPresentationModule
    ├── homeFeatureModule     # homePresentationModule
    └── appFeatureModule      # appNavigationModule (placeholder nav entries)
```

**Every module registers what it owns.** Components: `data/di/XDataModule` (repositories + data sources) and
`usecase/di/XUseCaseModule` (use cases). Features: `presentation/di/XPresentationModule` (ViewModels, validators,
`navEntry`) — **no use cases**. `:di` only assembles them; never add feature or component bindings to `coreModule`.
Koin DSL only — no `@Factory`/`@Single` annotations.

- `dispatchersModule` — a single `AppDispatchers(main, io, default, unconfined)`. Inject that one object; there are **no** `@IoDispatcher`-style annotation qualifiers.
- `networkModule` — `singleOf(::createHttpClient)`, which takes a `LocalAuthSessionDataSource` for the bearer refresh hook.
- `secureStorageModule` — owned by `core:security` (`com.store.core.security.di`), `:di` only includes it; expect/actual; Android binds `AndroidSecureStorage` (needs `androidContext()`), iOS `IosSecureStorage`, JVM `JvmSecureStorage(File(user.home, ".store_app"))`.

`SERVER_BASE_URL` is expect/actual in `core/network/.../ServerUrl.kt`. **The Android actual is a hardcoded LAN IP** — change `ServerUrl.android.kt` (and the IP whitelisted in each store's `network_security_config.xml`) when the dev machine's IP changes, or switch it to `10.0.2.2` for the emulator.

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

## App Architecture

`docs/reference/android-component-architecture.md` is inspiration from a different (pure-Android/Hilt) project —
never apply its rules here; this section wins.

**Multi-module structure:**
- `feature/<name>` — one module per feature: ViewModel (`BaseActionHandleViewModel`), ViewData, Compose UI,
  `navEntry` registration, feature strings. **Presentation only** — business logic lives in `component/`.
- `component/<entity>/` — business logic per domain entity: `auth`, `customer`, `product`.
  Planned (not built yet): `basket`, `order`, `payment`; admin is a role, not an entity.
- `core/` — shared infrastructure: `presentation`, `navigation`, `network`, `security`, `domain`, `utils`, `resources`.
- `shared/` — **client ↔ server wire contract** (domain models, `AuthRequest`/`AuthResponse`, `NetworkError`).
  The **server depends on it**, so it must never depend on core/component/feature and never carry UI.
- `build-logic/` — Gradle convention plugins.

**Data flow:** `Screen (Compose) → ViewModel → UseCase → Repository → DataSource → safeApiCall (Ktor)`

**Component module structure** (e.g. `component/customer/`) — all KMP (android + iOS + jvm):
- `model/` — domain models + the component's error type. `commonMain` only, no Compose/Android.
  Re-exports `:shared` types via `api(...)` for now; split into client-only models + mappers only when they diverge.
- `domain-api/` — repository interfaces. `commonMain` only.
- `usecase/` — use cases + `di/XUseCaseModule.kt`. `commonMain` only, plus `koin-core`.
- `data/` — repository impls, data sources, DTOs, `internal` mappers, `di/XDataModule.kt`. KMP + Ktor
  (OkHttp on Android/JVM, Darwin on iOS). DTOs and `NetworkError` never leave `data`. A data module keeps its own DTOs
  even when another component has a similar one (e.g. auth's `CustomerDto` for the `/auth/authorize` response).

**One-shot vs streams:** a request/command is a `suspend fun` returning `ApiResult<T, XError>` — in data sources,
repositories and use cases (`suspend operator fun invoke(...)`, no shared `UseCase` base interface). `Flow` is used
only for data that really changes over time (cache, local store, socket) and such functions are named `observeX()`.
Never wrap a single network call in `flow { emit(...) }`.

**Dependencies inside a component:** `domain-api → model`; `usecase → domain-api, model`; `data → domain-api, model`.
Features depend on `usecase` + `model` only — **never** `domain-api` or `data`.

**Between components:**
- *Entity* components (`customer`, `product`, `auth`) may depend only on another component's `model`.
- *Process* components (future `basket`, `order`, `payment`) — their `usecase` may depend on another component's
  `domain-api`, **for reads only**.
- `data` → another component's `data`: **never**. `usecase` → another `usecase`: never. No cycles.
- Session (userId / tokens) always comes from `core:security` (`LocalAuthSessionDataSource`) — never duplicated per repository.
- Writes that span several entities (e.g. order + clear basket) and money totals are done **by the server** in one
  transaction, not orchestrated on the client.

**DI:** see "Dependency Injection" — every module registers what it owns, `:di` only assembles.

**Errors, three levels:**
1. `core:network` `safeApiCall` → `ApiResult<T, NetworkError>` (wire format) + shared `NetworkError.toDataError()`
   (the one exhaustive `when` without `else` over all NetworkError values).
2. `component/<x>/data`: `internal fun NetworkError.toXError()` for the component's specific codes, delegating the rest
   to `toDataError()`. Every mapper has a test. Decisions that need the exact wire code (e.g. `isSessionTerminal` in
   `DefaultAuthRepository.signOut`) happen **before** mapping — the mapper deliberately loses detail.
3. Presentation maps `XError` to text. Features do it per error today (`SignInFailureHandler`, `HomeGraphInitializer`);
   a shared `DataError.toUiText()` in `core:presentation` does not exist yet — add it when a screen needs per-error texts.
`DataError` (`core:domain`): NoInternet, Timeout, Unauthorized, Forbidden, TooManyRequests, Server, Serialization, Unknown.
Component errors: `AuthError`, `CustomerError`, `ProductError` — each `sealed interface` with specific cases +
`data class Common(val error: DataError)`.

**Packages & namespaces:** package = Android namespace = module path — prefix `com.store`, `:` → `.`, `-` → `_`.
`:component:customer:domain-api` → `com.store.component.customer.domain_api`;
`:feature:products-overview` → `com.store.feature.products_overview`. Plugins derive it. `:shared` and the server keep
`org.cmp.store`; existing core namespaces are not renamed.

**Enforcement:** `config/architecture/rules.json` + `./gradlew validateArchitectureDependencies` (root plugin
`store.architecture`; also a separate CI job, report in `build/reports/architecture/violations.txt`).
- Checks every production `implementation` / `api` / `*Implementation` / `*Api` configuration (KMP included); test
  configurations are skipped.
- `{owner}` in `forbiddenDependency` is replaced with the source module's second path segment
  (`:component:customer:data` → `customer`), which is how "another component's …" rules are written.
- Run it **by name**, not by path: with `org.gradle.configureondemand=true`, `:validateArchitectureDependencies` would
  leave subprojects unconfigured (the task fails with that hint).
- `exceptions` need a `reason`; an exception that no longer matches a violation fails the task, so tech debt entries
  get deleted once fixed. The list is currently empty.

**Not adopted:** Router interfaces + app-level implementations — `AppNavigator` + `navEntry` already decouple features.

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
- **Token storage:** custom `SecureStorage` per platform (AndroidKeyStore AES/GCM + SharedPreferences; iOS Keychain; JVM AES/GCM file vault under `~/.store_app`). Not DataStore, not multiplatform-settings. The session contract is `LocalAuthSessionDataSource` (`core:security`,
used by `core:network` for bearer tokens and by `component/customer/data` for the user id); its implementations live in
`component/auth/data`: `DefaultLocalAuthSessionDataSource` (JVM; mutex-guarded, lazily hydrated cache) and
`FirebaseLocalAuthSessionDataSource` (Android/iOS; adds the Firebase uid fallback), picked by expect/actual
`provideAuthSessionDataSource`.
- **Transparent refresh:** Ktor `Auth` plugin `bearer { loadTokens / refreshTokens }` in `core/network/HttpClientFactory.kt`. The refresh call sets `AuthCircuitBreaker` to avoid recursion; on failure it signs out locally and returns null.
- **Errors:** wrap client calls in `safeApiCall { }` → `ApiResult<D, NetworkError>` with `onSuccess` / `onError` / `mapSuccess` / `mapError` from `:core:domain`.
  - HTTP errors: the `NetworkError` name in the body wins; without it the status decides (bare 404 → generic `NOT_FOUND`, never an entity-specific code).
  - Transport failures: timeouts (`HttpRequestTimeoutException`, `ConnectTimeoutException`, `SocketTimeoutException`, iOS `NSURLErrorTimedOut`) → `REQUEST_TIMEOUT`; unreachable host (JVM/Android `UnknownHostException` / `ConnectException` / …, iOS NSURLError −1009/−1003/−1004/−1005/−1020 via expect/actual `platformNetworkError()`) → `NO_INTERNET`; unreadable body → `SERIALIZATION`; anything else → `UNKNOWN`. Every transport failure is logged through `Logger`. `CancellationException` is always rethrown.
  - Server routes must throw `ApiException(status, NetworkError.X)` — never `call.respond(status, "free text")`, which the client cannot map.

## Testing

`:test` is a fixture module whose sources live in **`commonMain`** (not a test source set), so other modules consume it as a normal dependency via `module(ModulePath.TEST)` in their `commonTest`. It provides `BaseViewModelTest` / `runVmTest` (a `StandardTestDispatcher` wired into `AppDispatchers` plus `Dispatchers.setMain`) and the fakes (KMP has no `testFixtures`, so they live here): `FakeAuthRepository` (with a `gate`), `FakeCustomerRepository`, `FakeProductRepository` — each implementing its component's `domain-api` — and `FakeLocalAuthSessionDataSource`. Data-source fakes (`internal` types) stay in each component's `data/src/commonTest`. `com.store.test.network` holds the shared Ktor mock helpers (`testHttpClient`, `respondJson`, `jsonHeaders`) used by every data-source test. Its convention plugin re-exports kotlin-test, coroutines-test, Turbine, koin-test, compose-ui-test and the Ktor mock/serialization libraries as `api`.

Compose UI tests live in `feature/authentication/src/uiTest/` and are wired by `kotlin.srcDir` into **both** `jvmTest` (headless) and `androidDeviceTest` (emulator) — not via `dependsOn`, which is forbidden across source-set trees.

Covered today: `BaseActionHandleViewModel`, string utils, `HttpClientFactory` + `SessionRefreshCall`, `safeApiCall` + error mappers, every component (repositories, data sources, use cases), `AuthenticationViewModel`, the desktop OAuth stack, the Koin graph (`di/src/jvmTest/KoinGraphTest` — `sharedAppModule.verify()`, JVM-only because verify uses reflection), and the whole server (113 tests). Untested: `core:security`, `core:navigation`, `core:utils`, `feature:home:presentation`.

`KoinGraphTest` only checks constructor-reference definitions (`singleOf`/`factoryOf`/`viewModelOf`); lambdas like `single { … }` are opaque to it. If it fails on a type that is supplied at runtime (e.g. by the store's theme module), pass it via `verify(extraTypes = listOf(...))` instead of registering a dummy. Same for factory-function definitions: `singleOf(::createHttpClient)` makes verify inspect `HttpClient`'s own constructor, hence `HttpClientEngine` + `HttpClientConfig` in `extraTypes`.

## Key Versions

`gradle/libs.versions.toml` is the single source of truth. Highlights: Kotlin 2.3.21, AGP 9.2.1, KSP 2.3.9, compileSdk 37 / minSdk 24 / targetSdk 36, Java 21, Compose Multiplatform 1.11.1 (material3 1.9.0), Ktor 3.5.0 (server + client), Koin 4.2.1, Exposed 1.3.0 + sqlite-jdbc 3.53.2.0, Navigation3 UI 1.1.1, coroutines 1.11.0, kmpauth 2.5.0-alpha01, Firebase BOM 34.14.1, Turbine 1.2.1.

## Known Gaps (as of 2026-09-29)

- `:core:data` is registered but contains no sources.
- `feature:home:presentation` has no tests; `HomeGraphInitializer` (customer → cart products → total) is only checked by running the app.
- No shared `DataError.toUiText()` yet; the home screen shows one generic text per failing call.
- Without a connectivity service, `ConnectException` (e.g. the dev server is down) is reported as `NO_INTERNET`.
- `safeApiCall`'s status fallback still maps a bare 409 to the auth-specific `USER_ALREADY_EXISTS`.
- The iOS actual `PlatformNetworkError.ios.kt` is only compiled by an Xcode / macOS build — `allTests` on Windows skips it.
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
