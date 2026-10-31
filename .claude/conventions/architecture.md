## Module Structure

33 `include`s in `settings.gradle.kts` (type-safe project accessors enabled): 3 components × 4 layers, 2 features,
8 core modules, 7 client-app modules under `app/`, plus server/shared/di/test.

The layout follows JetBrains' default KMP structure for projects with a server: every client module lives under `app/`,
the client↔server contract stays at the root. `:shared` plays the role the JetBrains template calls `core` (that name
is taken by the client infrastructure modules). Each store is a small "app" of its own: a KMP library plus one module per
entry point. `app:shared` keeps the entry-point code both stores reuse.

```
app/                 # folder only, not a Gradle module
  shared/            # :app:shared — App.kt, AppViewModel, and the entry-point bases both stores reuse:
                     #   androidMain MainActivity + StoreApp (Application), jvmMain desktopApp(title, modules),
                     #   iosMain MainViewController(modules). No application plugin, no packaging, no iOS framework.
  athletica-plus/    # :app:athletica-plus — KMP library: theme (colors/strings), ThemeModule,
                     #   AthleticaPlusMainViewController; builds the iOS framework `StoresAthletica-plus`
    androidApp/      # com.android.application: AthleticaPlusApp : StoreApp, manifest, google-services.json, R8
    desktopApp/      # kotlin("jvm") + compose.desktop: main.kt, appicon/, Dmg/Msi/Deb packaging
  nutri-sport/       # same shape (iOS framework `StoresNutri-sport`)
  iosApp/            # ONE Xcode project with two targets (AthleticaPlusApp, NutriSportApp) + common/AppDelegate.swift
server/              # Ktor server — see `.claude/rules/server.md`
shared/              # Client↔server wire contract: Customer, CartItem, Product, AuthProvider/AuthRequest/AuthResponse,
                     #   NetworkError, Platform, Constants (SERVER_PORT = 8080)
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
di/                  # initializeKoin + module assembly (see `.claude/conventions/di.md`) + KoinGraphTest (jvmTest)
test/                # Shared test fixtures in commonMain — BaseViewModelTest, fakes, Ktor mock helpers
config/architecture/ # rules.json for validateArchitectureDependencies
docs/reference/      # android-component-architecture.md — inspiration from another (Hilt) project, not rules
build-logic/         # All Gradle convention plugins — never configure modules manually
gradle/              # libs.versions.toml (single source of truth for all versions)
```

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

**DI:** see `.claude/conventions/di.md` — every module registers what it owns, `:di` only assembles.

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

