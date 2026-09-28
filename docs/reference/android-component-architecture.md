# Android component architecture — reference

> ⚠️ **Reference material from ANOTHER project — not the rules of this repository.**
> It describes a pure Android app built on Hilt (JVM modules, JUnit4/Robolectric, Java 17, DataStore, detekt,
> `<TICKET>-N.` commits without `Co-Authored-By`). For CMP-Store-Server it is only a source of ideas for the
> migration to `component/<entity>/{model, domain-api, usecase, data}` (Store-10). Every decision has been adapted
> to KMP + Koin and recorded in `CLAUDE.md` (section "App Architecture"). If anything here contradicts `CLAUDE.md`,
> `CLAUDE.md` wins.

Context for an AI assistant.
Every statement was collected from the code as of 2026-09-13.
Numbers are approximate: they are grep counts over `git ls-files`.

Placeholders:
- `<app>` / `<App>` — the project prefix in package, plugin, module and class names. Take the real value from the code (`settings.gradle.kts`, `build_logic/convention/build.gradle.kts`).
- `<TICKET>` — the issue-tracker prefix.

---

## 1. Overview

- **Platform:** Android only. There are no `commonMain` / `iosMain` / `jvmMain` directories and no `kotlin.multiplatform` plugin. Pure JVM modules (`model`, `domain-api`, `usecase`, legacy `domain`) use `org.jetbrains.kotlin.jvm`. The file `core/encryption/.../IOS Source Code.kt` is only a ported reference, not KMP.
- **SDK** (`gradle/libs.versions.toml`, applied in `build_logic/convention/src/main/kotlin/ua/<app>/android/ProjectExtensions.kt`):
  - `minSdk = 23` (`minApi`)
  - `targetSdk = 36`, `compileSdk = 36` (`targetApi`)
  - Java/JVM target 17, `coreLibraryDesugaring` enabled
- **UI:** Jetpack Compose (Compose BOM `2026.02.00`, Material3). `@Composable` appears in about 595 files. There are no XML layout files (`res/layout*`). Navigation: `androidx.navigation:navigation-compose` 2.9.7 with type-safe routes (`toRoute()` in about 39 files).
- **Versions:**
  - Kotlin `2.3.21`
  - AGP `9.0.1` (key `gradle` in the version catalog)
  - KSP `2.3.9`
  - Hilt `2.59.1`
  - Ktor `3.4.0`
  - Coroutines `1.10.2`
- **Build types** (from `CLAUDE.md`): `debug` (test API), `release` (prod, minified), `qaTest`, `qaProd`.
- `gradle.properties`: `warningsAsErrors=true`, i.e. any Kotlin warning fails the build (`allWarningsAsErrors` in `ProjectExtensions.kt`).

## 2. Module structure

The full list is in `settings.gradle.kts`. Groups:

| Group | Modules | Purpose |
|---|---|---|
| `:app` | 1 | Hilt application, `MainActivity`, NavHost, Router implementations (`app/src/main/java/ua/<app>/android/mediator/routerImpl/`), **most DI modules** (`app/.../di/core`, `di/component`, `di/activity`) |
| `:core:*` | `core_ui`, `coroutines`, `build_config`, `network:data`, `network:domain`, `util`, `domain`, `encryption`, `firebase`, `server-log`, `analytics-api`, `analytics`, `analytics-firebase`, `analytics-test` | Infrastructure: Ktor clients, `DomainResult`, `CoroutineDispatchers`, Compose utilities, analytics, QR encryption (SpongyCastle) |
| `:component:*` | ~45 domain entities | Business logic per entity (basket, checkout, product, `<app>-pay`…) |
| `:shared:*` | 13 (`product`, `delivery`, `location`, `scanner`, `qr`…) | Reusable UI blocks / dialogs with their own ViewModels. Plugin `<app>.screen` |
| `:screen:*` | ~55 | Screens: ViewModel + UiState + Compose + Router interface + strings |
| `:designSystem` | 1 | Theme, tokens |
| `:detekt-rules` | 1 | Custom detekt rules (`<App>RuleSetProvider`) |
| `:baselineprofile` | 1 | Baseline profile / startup benchmark |
| `build_logic` | included build | Convention plugins `<app>.*` (see below) |

### Component module pattern: TWO layouts coexist

1. **New, 4 modules:** `model` / `domain-api` / `usecase` / `data`.
   - Components with this layout: `<app>-pay`, `<app>-pay-qr`, `widget`, `quick-access`, `appConfig`.
   - Partially: `install-referrer` (`domain` + `usecase` + `data`), `<app>-cafe` (`usecase` only).
   - Plugins:
     - `model` and `domain-api` → `<app>.jvm.library`;
     - `usecase` → `<app>.component.usecase`;
     - `data` → `<app>.component.data`.
2. **Legacy, 2 modules:** `domain` / `data`. Used by basket, checkout, catalog, product, cashback and most others. The `domain` module (plugin `<app>.jvm.library`) contains all of:
   - the repository interface;
   - the models;
   - the use cases.

   Example: `component/basket/domain/.../BasketRepository.kt`, `usecase/ClearBasketUseCase(Impl).kt`.
3. **Special case `user`:** `data_api` / `data_impl` / `usecase_api` / `usecase_impl`.
4. **Modules without submodules:** `:component:appCache`, `:component:analytics`.

New modules must be kebab-case. Legacy snake_case is allowed only via the allowlist in `config/legacy-module-names.txt`. Checked by the `validateModuleNames` task.

### Convention plugins (`build_logic`)

`build_logic` is wired as an included build via `pluginManagement { includeBuild("build_logic") }` in `settings.gradle.kts`.

**Registration and access:**
- Plugins are registered in `build_logic/convention/build.gradle.kts` (`gradlePlugin { plugins { register(...) } }`).
- Modules apply them through the version catalog: `alias(libs.plugins.<app>.screen)` etc. (section `[plugins]` in `gradle/libs.versions.toml`, `version = "unspecified"`).
- The version catalog is available inside plugins as `libs` (`ua/<app>/android/VersionCatalogExtensions.kt`).
- `build_logic/convention/build.gradle.kts` only declares AGP, Kotlin, detekt, kover and the Compose Gradle plugin as `compileOnly`.

**Plugins** (implementations in `build_logic/convention/src/main/kotlin/`; count = number of `build.gradle.kts` files applying the plugin):

| ID | Class | Applies plugins | Adds dependencies / configuration | Used by |
|---|---|---|---|---|
| `<app>.application` | `ApplicationConventionPlugin` | android.application, ksp, hilt, compose, serialization, `<app>.detekt` | `targetSdk`, `buildFeatures.compose`, lint; core-ktx, splashscreen, serialization-json, kotlin-reflect, `bundles.hilt` + `hilt-compiler` (ksp), rebugger (debug), takt, Firebase BOM + `bundles.firebase` | `:app` (1) |
| `<app>.screen` | `ScreenConventionPlugin` | android.library, ksp, hilt, serialization, compose, `<app>.detekt`, `<app>.kover` | `buildFeatures.compose`, `unitTests.isIncludeAndroidResources = true`, lint; `:core:coroutines`, `:core:core_ui`, `:core:firebase`, serialization-json, collections-immutable, `bundles.hilt`, crashlytics, `bundles.analytic`; test: junit, robolectric, compose-ui-test-junit4 (+ test-manifest in debug) | `screen/*` and `shared/*` (63) |
| `<app>.android.library` | `AndroidLibraryConventionPlugin` | android.library, serialization, ksp, `<app>.detekt`, `<app>.kover` | lint; serialization-json, core-ktx, javax-inject; test: junit. **Does not apply Hilt.** | legacy `data`, `core/*` etc. (42) |
| `<app>.android.library.compose` | `AndroidLibraryComposeConventionPlugin` | android.library, compose, `<app>.detekt`, `<app>.kover` | `buildFeatures.compose`, lint, Compose BOM + `bundles.compose.all` | `:designSystem`, `:core:core_ui`, `:core:firebase` (3) |
| `<app>.jvm.library` | `JvmLibraryConventionPlugin` | kotlin.jvm, `<app>.detekt`, `<app>.kover` | coroutines-core; test: junit | `model`, `domain-api`, legacy `domain` (55) |
| `<app>.component.usecase` | `ComponentUsecaseConventionPlugin` | kotlin.jvm, `<app>.detekt`, `<app>.kover` | coroutines-core, javax-inject; test: junit | `component/*/usecase` and part of legacy `domain` (9) |
| `<app>.component.data` | `ComponentDataConventionPlugin` | android.library, serialization, ksp, hilt, `<app>.detekt`, `<app>.kover` | lint; `:core:network:data`, serialization-json, hilt, javax-inject, `hilt-compiler` (ksp); test: junit | new `component/*/data` (6) |
| `<app>.detekt` | `DetektConventionPlugin` | io.gitlab.arturbosch.detekt | `config/detekt/config.yml`, `buildUponDefaultConfig`, `allRules = true`, `parallel`, `autoCorrect = (CI == null)`, per-module baseline `detekt-baseline.xml`; HTML + XML reports; `detektPlugins`: detekt-formatting, compose-rules, `:detekt-rules` | every plugin except `<app>.kover` |
| `<app>.kover` | `KoverConventionPlugin` | org.jetbrains.kotlinx.kover | — (aggregated in the root `build.gradle.kts`) | every plugin except `<app>.application` and `<app>.detekt` |

**Shared functions** (`build_logic/convention/src/main/kotlin/ua/<app>/android/ProjectExtensions.kt`):
- **`configureKotlinAndroid()`:**
  - `compileSdk` / `minSdk` from the catalog;
  - Java 17;
  - `isCoreLibraryDesugaringEnabled` + `desugarJdkLibs`;
  - `failOnNoDiscoveredTests = false`.
- **`configureKotlinJvm()`:** Java 17.
- **`configureKotlin()`** (Android and JVM):
  - `jvmTarget = 17`;
  - `allWarningsAsErrors` from the `warningsAsErrors` property;
  - global opt-ins: `ExperimentalCoroutinesApi`, `ExperimentalTime`, `ExperimentalUuidApi`.
- **`configureAndroidCompose()`:**
  - Compose BOM + `bundles.compose.all`, `ui-tooling` (debug);
  - opt-ins: `ExperimentalComposeUiApi`, `ExperimentalFoundationApi`, `ExperimentalMaterial3Api`;
  - `stabilityConfigurationFiles` = `config/compose/stability-config.conf`;
  - compose reports are written only with `-PenableComposeReports=true`.
- **`Lint.configureLint()`:**
  - `abortOnError = true`, `checkDependencies = false`;
  - `config/lint/lint.xml`;
  - per-module `lint-baseline.xml`.
- **`DependencyHandlerScopeExtensions.kt`:** helpers `implementation` / `testImplementation` / `debugImplementation` / `ksp` / `lintChecks` for use inside plugins.

**The root `build.gradle.kts` adds tasks on top of the plugins:**
- `unitTest` — `dependsOn` `:test` for `kotlin.jvm` and `:testDebugUnitTest` for android library / application;
- `validateArchitectureDependencies` — reads `config/architecture/rules.json`, looks only at the `implementation` / `api` configurations;
- `validateModuleNames` — kebab-case + allowlist;
- `verifyComposeMetrics` — `unstable` classes from the compose reports against `config/compose/unstable-allowlist.txt`;
- `installGitHooks` — copies `config/git-hooks` into `.git/hooks`;
- Kover aggregation with `minBound(5)`, OWASP `dependencyCheck`, `dependencyUpdates`, CycloneDX (`:app` → `releaseRuntimeClasspath`).

**What the plugins do NOT do** (added by hand in the module's `build.gradle.kts`):
- `namespace`;
- dependencies on other component modules;
- `kotlinx-coroutines-test`;
- `testFixtures`;
- `kotlin.parcelize`;
- Hilt for `<app>.android.library`.

## 3. Architecture

**Pattern:** Clean Architecture (layers in separate Gradle modules) + MVVM with an MVI-like screen contract. Evidence:

- **UseCase.** About 350 declarations in three styles:
  - `fun interface XUseCase` — about 174, the most common;
  - `class XUseCaseImpl` — about 110;
  - `class XUseCase @Inject constructor` — about 41, the new style in `usecase` modules.
- **Repository interface + impl:**
  - `component/basket/domain/.../BasketRepository.kt`
  - `component/basket/data/.../BasketRepositoryImpl.kt`
- **ViewModel + UiState + Actions + Router:**
  - `screen/<app>-pay/.../<App>PayViewModel.kt`
  - `actions/<App>PayActions.kt`
  - `<App>PayUiState`
  - `screen/basket/.../navigation/BasketRouter.kt` → `app/.../routerImpl/BasketRouterImpl.kt`

**Dependency direction:**
- Layers: `Screen → UseCase → Repository interface ← RepositoryImpl → DataSource → <App>HttpClient / DataStore`.
- Module rules are defined in `config/architecture/rules.json` and checked by `validateArchitectureDependencies`:
  - screen ↛ `component:*:data`
  - screen ↛ `domain-api`
  - usecase ↛ another usecase
  - data ↛ usecase / screen
  - core ↛ component / screen / shared
  - model ↛ other modules of the component

**How well this holds in practice:**
- ✅ `domain` / `domain-api` / `model` / `usecase` contain no imports of `android.*`, `androidx.*`, `io.ktor`, `kotlinx.serialization`. DTOs with `@Serializable` live only in `data`.
- ✅ The `domain` / `model` / `usecase` modules are pure Kotlin/JVM (plugins `<app>.jvm.library` / `<app>.component.usecase`), so the platform physically cannot be added there.
- ⚠️ Legacy screens depend on `component:*:domain`, which also holds the repository interface. The `domain-api` rule does not cover them, so a screen technically has access to the repository. Direct access is caught by the detekt rule `NoRepositoryInViewModel`. Example: `screen/basket/build.gradle.kts` → `projects.component.basket.domain`.
- ⚠️ Legacy domain modules transitively pull in other components via `api(...)`. Example: `component/basket/domain/build.gradle.kts` → `api(projects.component.product.domain)`, `api(projects.component.deliveryType.domain)`.
- ⚠️ Documented tech debt (the `exceptions` section of `rules.json`):
  - `:core:firebase` → `:component:product:domain` and `:component:deliveryType:domain`;
  - `:screen:viewed_purchased_product_list` → `:component:viewType:data` (use cases live in data there).
- ⚠️ `ComposableNoDomainModel` (detekt) forbids domain models in composables. UI models carry the `Ui` suffix (about 21 sealed `*Ui` types).

**Example 1 — legacy fun-interface use case, assembled in DI with a method reference:**
```kotlin
// component/basket/domain/.../usecase/GetBasketUseCase.kt
fun interface GetBasketUseCase : () -> Basket?

// app/.../di/component/BasketViewModelComponentModule.kt
@Provides
fun provideObserveBasketProductUseCase(basketRepository: BasketRepository): ObserveBasketProductUseCase {
    return ObserveBasketProductUseCase(basketRepository::observeProduct)
}
```

**Example 2 — new use case with `@Inject`, several repositories:**
```kotlin
// component/<app>-pay/usecase/.../IsAllow<App>PayUseCase.kt
class IsAllow<App>PayUseCase @Inject constructor(
    private val basketRepository: <App>PayRepository,
    private val locationRepository: <App>PayLocationRepository,
) {
    suspend operator fun invoke(): <App>PayResult<<App>PayBasketResult> =
        locationRepository.withFreshLocation { location -> basketRepository.isAllow(location) }
}
```

**Screen contract** (see `<App>PayViewModel`):
- `val uiState: StateFlow<XUiState>` — `combine(...)` + `.stateIn(viewModelScope, SharingStarted.whileSubscribed(), XUiState.EMPTY)`;
- a private `MutableStateFlow<XMutableState>`;
- `Channel<XEvent>(Channel.BUFFERED)` → `receiveAsFlow()`;
- the ViewModel implements `XActions`.

The contract is not introduced everywhere: there are about 46 `*UiState.kt` files and about 27 `*Actions.kt` files, against about 117 ViewModel classes.

## 4. Dependency Injection

- **Framework:** Hilt (`com.google.dagger:hilt-android` 2.59.1, KSP), `hilt-navigation-compose`, `hilt-work`.
  - Hilt is added by the plugins `<app>.application`, `<app>.screen`, `<app>.component.data`.
  - `usecase` modules get only `javax.inject:javax.inject`.
- **Organisation — in practice mostly global, in `:app`:**
  - about 122 `@Module` files in `app/src/main/java/ua/<app>/android/di/` (`core/NetworkModule.kt`, `core/CoreModule.kt`, `component/<Name>SingletonModule.kt` + `component/<Name>ViewModelComponentModule.kt`);
  - about 15 `@Module` files inside the components themselves, only in the newer ones: `analytics`, `appConfig`, `cheque`, `<app>-pay`, `<app>-pay-qr`, `install-referrer`, `quick-access`, `widget` (`component/<name>/data/.../di/`).

  > `CLAUDE.md` declares a decentralised approach ("app/ only connects"). That is the target state. Legacy components are still wired from `app/di/component`.
- **Scopes:**
  - `@InstallIn(ViewModelComponent::class)` — about 94, use cases of legacy components;
  - `@InstallIn(SingletonComponent::class)` — about 44, repositories, clients.
- **Patterns:**
  - Constructor injection. Field injection is forbidden (detekt `NoFieldInjection`).
  - Legacy `data` classes have no `@Inject` and are created by hand in `@Provides`. Example: `BasketRepositoryImpl(basketRemoteDataSource, applicationScope)` in `BasketComponentSingletonModule`.
  - New components: `@Inject constructor` + `@Binds`.
  - HTTP qualifiers `@AuthorizedClient` / `@UnauthorizedClient` (`core/network/data/.../client/`).
  - `CoroutineDispatchers` is provided in `CoreModule.provideCoroutineDispatchers()`. The application `CoroutineScope` lives there too (`SupervisorJob() + Dispatchers.IO`).
  - `Lazy<…>` in screen/shared modules: about 23 usages.
  - Navigation arguments:
    - `screen/` ViewModels — via `SavedStateHandle.toRoute()`;
    - `shared/` ViewModels — via `@AssistedInject` (5 files).
- `expect`/`actual` is not used.

## 5. Networking and data

**HTTP client:** Ktor 3 with the OkHttp engine.
- `core/network/data/.../client/UnauthorizedHttpClient.kt`:
  - `ContentNegotiation` with a shared `Json` instance (`core/network/data/.../<App>Json.kt`);
  - `HttpTimeout(requestTimeoutMillis = 20000)`;
  - `Logging(LogLevel.ALL)` filtered by the API host;
  - `expectSuccess = false`;
  - `defaultRequest`: HTTPS, `baseUrl`, headers `deviceTime` / `user-info`;
  - technical-work interceptor → `GlobalAppStateDispatcher`.
- `AuthorizedHttpClient.kt`: `config {}` on top of the unauthorised client + `Auth { bearer }`:
  - token refresh under a `Mutex`;
  - session migration (`MigrateSession`);
  - the `TokenSchemeRewriter` plugin rewrites `Bearer` → `Token`;
  - on a failed refresh: `tokensLocalDataSource.clear()` + `UnauthorizedEventDispatcher.emit()`.
- `<App>HttpClient.kt` — a wrapper with two methods:
  - `safeRequest<T>` → `NetworkResult<T>`;
  - `safeRequestPreservingBody<T>` → `NetworkResultWithBody<T>`, for when the response body is still needed with `code != 0` (`AddItemsInBasket`).
- **There are two clients**: `@UnauthorizedClient` (login/OTP, refresh) and `@AuthorizedClient` (everything else). Both are provided in `app/.../di/core/NetworkModule.kt`.
- **API protocol:** POST with `BaseRequest("MethodName", body)`. The response arrives in `ResponseWrapper<T>` / `ErrorResponseWrapper`. HTTP 200 + `error.code == 0` means success.

**Error handling — sealed hierarchies, exceptions never escape:**
- `NetworkResult<T>` (`core/network/data/.../model/NetworkResult.kt`):
  - `Success(data, headers)`
  - `ServerError(code, message)`
  - `UnknownError(throwable)`
  - `UnknownServerError`
  - `NoInternetConnection`
  - `TimeOut`
- In `<App>HttpClient.safeRequest`:
  - `UnknownHostException` → `NoInternetConnection` + an event in `NetworkRequestWithoutInternetDispatcher`;
  - `ConnectException` → `NoInternetConnection` or `UnknownError` depending on `NetworkConnectivityService`;
  - `TimeoutException` → `TimeOut`;
  - serialization and IO errors → `UnknownError`;
  - **`CancellationException` is rethrown**;
  - HTTP ≠ 200 → `UnknownServerError`, HTTP 408 → `TimeOut`.
- `DomainResult<T>` (`core/domain/.../DomainResult.kt`): `Success`, `NoInternetConnection`, `Failure(message)`. Utilities: `onSuccess`, `onFailure`, `getOrNull`, `mapToDomainResult`.
- The mapper `NetworkResult.toDomainResult { }` is used in about 28 files. ⚠️ It collapses `TimeOut` / `UnknownError` / `UnknownServerError` into `Failure("")`.
- Operation-specific sealed results in domain/model: `UpdateProductQuantityResult`, `AddProductsToBasketResult`, `LogoutResult`, `DeleteAccountResult`, `SetCashbackSpendingResult`…
- New style: a dedicated `<App>PayResult` / `<App>PayError` in `component/<app>-pay/model`.

**Local storage — DataStore only:**
- Preferences DataStore:
  - `core/network/data/.../TokensLocalDataSourceImpl.kt` — tokens; `SharedPreferencesMigration` from the old SharedPreferences, no encryption visible in the class;
  - `app/.../mediator/PushNotificationSettingsManagerImpl.kt`.
- Typed DataStore with a serializer and `ReplaceFileCorruptionHandler`:
  - `component/cashback/data/.../CashbackLocalDataSourceImpl.kt`;
  - `core/network/data/.../serializer/JsonDataStoreSerializer.kt`;
  - `*LocalDataSourceImpl` in banner, checkout, masterpass, qr, timeSlot, widget, appConfig, user etc.
- **Room / SQLDelight / Realm are not used.** No Android Keystore for secrets / `EncryptedSharedPreferences` was found. `java.security.KeyStore` exists only for SSL trust in `component/deliveryType/data/.../MapSslConfiguratorImpl.kt`. QR data is encrypted with SpongyCastle: `core/encryption/QrEncryption.kt`.

## 6. Models and mappers: from UI to HTTP

### 6.1. Map of models by layer and module

Read direction — from the screen to the network. Suffixes are taken from file names in `src/main` (number of files with that ending).

| Layer | Module | Model | Suffix / example | Annotations / notes |
|---|---|---|---|---|
| Navigation | `screen/<name>/navigation/` | screen route | `*Destination` (about 64): `BasketDestination` (`data object`), `CashbackPinCodeOtpDestination(id, …)` (`data class` with arguments) | `@Serializable`; read in the ViewModel via `SavedStateHandle.toRoute<…>()`; a factory `operator fun invoke(domainModel, …)` in the `companion` builds the route from a domain model |
| UI state | `screen/<name>/model/` or `state/` | whole-screen state | `*UiState` (about 46): `BasketUiState`, `<App>PayUiState` | `@Immutable data class`, `companion` with `INITIAL` / `EMPTY`; fields are `*Ui`, `ImmutableList`, flags, sealed `*ScreenState` |
| UI state (private) | `screen/<name>/model/` / `state/` | mutable flags | `*MutableState`: `BasketMutableState`, `<App>PayMutableState` | only inside the ViewModel, `copy()` via `update {}` |
| Events | `screen/<name>/model/` | one-time event | `*Event`: `BasketEvent`, `<App>PayEvent` | `sealed interface`, through a `Channel` |
| UI model | `screen/<name>/model/`, shared ones in `shared/*`, base ones in `core/core_ui/.../ui/model/` | display-ready data | `*Ui` (about 114): `BasketUi`, `<App>PayBagUi`; `ProductUi` in `core/core_ui/.../model/product/ProductUi.kt` | `@Immutable` / `@Stable`; **already formatted strings** (`total = "123.00 ₴"`), `ImmutableList`, `EMPTY`; may implement UI interfaces (`<App>PayBagUi : BasketButtonProduct`) |
| Domain (legacy) | `component/<name>/domain/.../model/` | business model | no suffix: `Basket`, `FullBasketInfo`, `BasketMessage`, `PromoInfo` | pure Kotlin, `BigDecimal` for money / weight, sealed messages; no `@Serializable` |
| Domain (new) | `component/<name>/model/` | business model + result + error | `<App>PayBasket`, `<App>PayBasketItem`, `<App>PayResult<T>`, `<App>PayError`, value objects `ProductBarcode`, `ExciseBarcode` | pure Kotlin, JVM module |
| Operation result | `core/domain` / `component/*/domain` / `component/*/model` | response wrapper | `DomainResult<T>`; operation-specific `UpdateProductQuantityResult`, `AddProductsToBasketResult`; new style — `<App>PayResult<T>` + `<App>PayError` | `sealed interface` |
| Request DTO | `component/<name>/data/.../cloud/request/` | request body | `*Request` (about 89): `GetBasketRequest(fromCache)`, `IsAllow<App>PayRequest(lat, lon)` | `@Serializable` + `@SerialName`; built in the **Repository** from domain parameters |
| Response DTO | `component/<name>/data/.../cloud/response/` (new: also `cloud/model/response/`) | response body | `*Response` (about 95), `*Dto` (about 69), legacy `*Cloud` (9): `BasketResponse`, `DeliveryInfoDto`, `MessageCloud`, `<App>PayBasketDto` | `@Serializable`; nullable fields and primitives (`Double`) as in the JSON |
| Network envelope | `core/network/data/.../model/` | protocol wrapper | `BaseRequest<T>(method, data)` (`"Method"` / `"Data"`), `EmptyData` for requests without a body, `ResponseWrapper<T>(result)` (`@JsonNames("result", "v2Result")`), `ErrorResponseWrapper(error: ErrorResponse(errorCode, errorString, errorTrace))` | shared by all components |
| Network result | `core/network/data/.../model/` | HTTP call result | `NetworkResult<T>`, `NetworkResultWithBody<T>`; the new component adds its own `<App>PayServerResponse<T>` (`Success` / `PartialSuccess` / `Failure` / `NoInternet` / `TimeOut` / `Generic`) in `data/.../cloud/` | `sealed interface` |
| Local cache | `component/<name>/data/.../local/` | DataStore model | `*Cache` (18), `*Dbo` (4): `CashbackCache`, `BannerDbo` | `@Serializable`, `companion EMPTY`, `toDomain()` + `companion fromDomain()` |

**Visibility.** Almost all DTOs / Requests / Responses in `data` are declared `public` (about 231 vs 1 `internal`). In the new pay component the mappers are `internal`, the models are not. One component's DTOs can be reused in another: `component/basket/data` depends on `component/product/data` and maps `ProductDto` through its `List<ProductDto>.toDomain()`.

### 6.2. Data flow (legacy component, basket as the example)

```
Compose (BasketScreen)
  ▲ BasketUiState { basket: BasketUi, … }                     screen/basket/model
  │   BasketDomainToUiMapper.map(Basket, DeliveryType?) → BasketUi   screen/basket
  │     └ ProductDomainToUiMapper.map(List<Product>)          shared/product
BasketViewModel
  ▲ DomainResult<Basket?> / Flow<Basket?>                     component/basket/domain
UseCase (ClearBasketUseCaseImpl / fun interface)              component/basket/domain
  ▲ DomainResult<FullBasketInfo?>
BasketRepositoryImpl                                          component/basket/data
  │ when (NetworkResult) → DomainResult                       (manual when or toDomainResult { })
  │ BasketResponse.toFullBasketInfo() → FullBasketInfo(Basket, DeliveryType)
  │   ProductDto.toDomain()                                   component/product/data
  ▼ GetBasketRequest(fromCache)
BasketRemoteDataSourceImpl                                    component/basket/data/cloud
  │ BaseRequest("GetBasketViewV2", request)
  ▼ client.safeRequest<ResponseWrapper<BasketResponse?>> { post { setBody(…) } }
<App>HttpClient → Ktor (AuthorizedHttpClient)                 core/network/data
  ▲ NetworkResult<ResponseWrapper<BasketResponse?>>
```

Steps:
1. **Repository → Request.** The repository builds a `*Request` from domain arguments: `basketRemoteDataSource.getBasket(GetBasketRequest(fromCache))`.
2. **RemoteDataSource → HTTP.** It only wraps the request in `BaseRequest("<ApiMethod>", request)` and calls `safeRequest` / `safeRequestPreservingBody`. It returns the **raw** `NetworkResult<ResponseWrapper<Dto>>` and maps nothing.
3. **HTTP → NetworkResult.** `<App>HttpClient` first decodes `ErrorResponseWrapper`: `errorCode == 0` → `Success(body, headers)`, otherwise `ServerError(code, message)`. Everything else becomes `TimeOut` / `NoInternetConnection` / `UnknownError` / `UnknownServerError`.
4. **NetworkResult → Domain** (in the Repository). Two ways:
   - an exhaustive `when` listing every branch explicitly (`BasketRepositoryImpl.clear()`, `updateProductQuantity()` → `UpdateProductQuantityResult`);
   - `result.toDomainResult { wrapper -> wrapper.result.toDomain() }` (`removeUnavailableItemsFromBasket`, `applyOrder`).

   In both cases `TimeOut` / `UnknownError` / `UnknownServerError` → `Failure("")`, and `ServerError.message` → `Failure(message)`.
5. **DTO → Domain model.** Extension functions next to the DTO in `data`:
   - `fun BasketResponse.toFullBasketInfo()` in the same file `cloud/response/BasketResponse.kt`;
   - `fun ProductDto.toDomain()` / `fun List<ProductDto>.toDomain()` in `ProductDto.kt`.

   This is where `Double` → `BigDecimal.setScale(2, HALF_EVEN)` happens, server string keys are parsed (`"basketMaxWeight"` → `BasketMessage.Error.MaxWeight`), and `null` → `emptyList()`.
6. **Domain → UseCase → ViewModel** without a type change. A use case may combine several repositories and returns `DomainResult` / a domain model.
7. **Domain → UI** (in screen / shared). Details in 6.4.

### 6.3. Data flow (new 4-module component, pay as the example)

```
Compose
  ▲ <App>PayUiState                                             screen/<app>-pay/state
  │   <App>PayUiStateFactory.create(basket, filial, scanSignal, mutable)  screen/<app>-pay/di
  │     └ <App>PayMappers { product, bag, priceLabel }          screen/<app>-pay/mapper
  │   <App>PayErrorMapping: <App>PayError → MutableState / Event / sheet error (StringRetrieval)
<App>PayViewModel
  ▲ <App>PayResult<T> / Flow<<App>PayBasket?>                   component/<app>-pay/model
IsAllow<App>PayUseCase @Inject                                  component/<app>-pay/usecase
  ▲ <App>PayRepository (interface)                              component/<app>-pay/domain-api
<App>PayRepositoryImpl                                          component/<app>-pay/data
  │ <App>PayServerResponse / NetworkResult → <App>PayResult     data/cloud/mapper/NetworkResultMapper.kt
  │ errorCode → <App>PayError                                   data/cloud/mapper/ErrorCodeMapper.kt
  │ Dto.toDomain() / toBasketResult() / toBagListData()         data/cloud/mapper/<App>PayDtoMapper.kt
  ▼ IsAllow<App>PayRequest(lat, lon)
<App>PayRemoteDataSourceImpl                                    data/cloud
  ▼ safeRequestPreservingBody<<App>PayCombinedDto<…>> / safeRequest<ResponseWrapper<…>>
```

Differences from legacy:
- **DTO → domain mappers are extracted** into a separate package `data/.../cloud/mapper/` as `internal` extension functions (`<App>PayDtoMapper.kt`) instead of living in the DTO file.
- **The error is typed, not a string.** `NetworkResult<ResponseWrapper<T>>.to<App>PayResult(transform)` maps **every** branch: `ServerError(code)` → `code.to<App>PayError(message)` (codes 526–533 → specific `data object`s), `NoInternetConnection` → `NoInternet`, `TimeOut` → `TimeOut`, `UnknownError` → `Generic(throwable.message)`. Error codes are `internal const val ERROR_CODE_*` in `ErrorCodeMapper.kt`.
- **Partial success is not lost.** Where the server returns a body together with a non-zero code, the RemoteDataSource returns its own `<App>PayServerResponse.PartialSuccess(data, error)` built on `safeRequestPreservingBody`.
- **The repository holds state** (a `MutableStateFlow` with `<App>PayBasketResult`) and exposes derived `Flow`s (`observeBasket()`, `observeFilial()`, `observeScanSignal()`) with `distinctUntilChanged()`.
- **UI state is assembled by a factory.** `<App>PayUiStateFactory` (injected) combines domain flows + `MutableState` into the `UiState`. Mappers are aggregated in `<App>PayMappers` (an `@Inject` holder), error → UI mapping lives in a separate `<App>PayErrorMapping`.

### 6.4. Domain → UI: mapping styles

About 89 `toUi*` extension functions in main (mostly in `screen`, 8 files in `shared`) and about 27 `*Mapper` classes / interfaces:

1. **A mapper class with dependencies** (when strings, formatting or other mappers are needed):
   - named `<Entity>DomainToUiMapper`, method `fun map(domain, …): XUi`;
   - depends on `StringRetrieval` (`core/core_ui/.../StringRetrieval.kt`: `getString`, `getQuantityString`, `getStringArray`) for resources and plurals;
   - **interface + `Impl`** (legacy, `@Provides` in DI): `NotificationsDomainToUiMapper(Impl)`, `PersonalPromotionDomainToUiMapper(Impl)`, `PersonalPromotionsDomainToUiMapper(Impl)` in `shared`;
   - **class without an interface**: `BasketDomainToUiMapper(stringRetrieval, productDomainToUiMapper)` (constructor without `@Inject`), `<App>PayBagDomainToUiMapper @Inject constructor(priceLabel)`;
   - shared ones: `ProductDomainToUiMapper` (`shared/product`), `DeliveryInfoDomainToUiMapper` (`shared/delivery`);
   - specialised UI formatters: `CountdownUiMapper(Impl)`, `BirthdayUiMapper(Impl)`, `PhoneNumberToUiFormatMapper(Impl)` (`core/core_ui`), `PriceLabel` (`screen/<app>-pay/mapper`).
2. **An extension function in the UI model file** (when no strings are needed): `fun Category.toUi(isPromo)` / `fun List<Category>.toUi()` in `screen/catalog/.../model/CategoryUi.kt`, `fun Set.toUi()` in `SetUi.kt`, `fun OrderAnswer.toUi()` in `OrderAnswerUi.kt`.
3. **A private extension in the ViewModel** (2 ViewModel files): `private fun List<Product>.toUi()` in `BasketViewModel.kt`.

What a Domain → UI mapper does (see `BasketDomainToUiMapper`):
- formats `BigDecimal` into a ready string through a string resource with the currency;
- picks plurals (`R.plurals.basket_products_count_format`);
- filters and unwraps sealed messages (`filterIsInstance<BasketMessage.General>()`);
- computes a UI status (`BasketOrderStatus`);
- converts lists into `ImmutableList` / `PersistentList`.

**Domain → screen state for errors:**
- legacy: `DomainResult.Failure(message)` / `NoInternetConnection` → `*ScreenState` / snackbar in the ViewModel;
- new style: `<App>PayError` → `<App>PayErrorMapping.entryState()` / `operationEvent()`, which return an updated `MutableState` or an `Event` with text from `StringRetrieval`.

Detekt `ComposableNoDomainModel` forbids domain models in composables. Imports of `component.*.domain` / `model` exist in about 11 screen files with `@Composable`; these are candidate violations, not checked individually (possibly in the baseline).

### 6.5. Local models (DataStore)

```
Repository → LocalDataSource.save(domain) → XCache.fromDomain(domain) → DataStore.updateData
Repository ← LocalDataSource.observe(): Flow<Domain?> ← dataStore.data.map { it?.toDomain() }
```

Example: `component/cashback/data/.../local/CashbackCache.kt`:
- `@Serializable data class CashbackCache(amount: String)`;
- `toDomain(): CashbackAmount?` (returns `null` for `EMPTY`);
- `companion fun fromDomain(info)` and `EMPTY`.

`BigDecimal` is stored as a string. The mapper is a set of methods on the cache model itself. A separate mapper class exists in `component/user`: `UserCacheToDomainMapper(Impl)`, and `CloudNotificationsToDomainMapper(Impl)` in `notification`.

### 6.6. Mapper tests

About 15 `*MapperTest` files: `<App>PayDtoMapperTest`, `NetworkResultMapperTest` (`component/<app>-pay/data`), `PersonalPromotionDomainToUiMapperImplTest` with `FakeStringRetrievalImpl` (`screen/personal_promotion_details`), `PaymentTypeMapperTest`, `BannerDboTest`, `ChequeProductDtoTest`.

## 7. Coroutines & Concurrency

- **Dispatchers are injected.** The interface `core/coroutines/.../CoroutineDispatchers.kt` has `io()`, `main()`, `immediate()`, `default()`. For tests there are `testFixtures`: `TestCoroutineDispatchers(testDispatcher)`. Detekt `NoHardcodedDispatcher` forbids `Dispatchers.*`.
  - Leftovers not yet cleaned up:
    - `app/.../di/core/CoreModule.kt:139` (`applicationScope`);
    - `component/personalPromotions/data/.../PersonalPromotionsRepositoryImpl.kt:38`;
    - `screen/redact_image/.../ImageCropper.kt:338`;
    - a default parameter in `core/core_ui/.../compose/util/Flow.kt`.
- **The `ExperimentalCoroutinesApi` opt-in** is enabled globally in the convention plugins (`configureKotlin()`), so `@OptIn` for `flatMapLatest` etc. is not needed.
- **`CancellationException`** is rethrown explicitly:
  - in `<App>HttpClient`;
  - in `AuthorizedHttpClient.performRefreshTokens`;
  - in `BasketRepositoryImpl`, `ChequeRepositoryImpl`;
  - in `CompositeAnalytics`, `MainActivity`, `PushNotificationSettingsManagerImpl`.

  `FavoriteRepositoryImpl` itself throws `CancellationException(SUPERSEDED_MESSAGE)` to cancel stale requests. There are about 27 broad `catch (Exception|Throwable)` blocks in main.
- **Timeout:**
  - network — `HttpTimeout` 20 s;
  - in the ViewModel — `withTimeoutOrNull` for location (`<App>PayViewModel`, `ScannerViewModel`) and cache (`HomeViewModel.cachedValueOrNull`).
- **Mutex:** `kotlinx.coroutines.sync.Mutex` to serialise token refreshes.
- **`runCatching`:** about 13 files, used sparingly. No custom wrapper like `suspendRunCatching` was found.
- **Application scope:** repositories receive a `CoroutineScope` through DI (`BasketRepositoryImpl(…, applicationScope)`).
- **Flow — typical patterns** (occurrence counts):
  - `MutableStateFlow` — about 411: ViewModel state and repository caches;
  - `stateIn` — about 119, with `SharingStarted.whileSubscribed()` = `WhileSubscribed(5000)` (`core/core_ui/.../extension/FlowExtensions.kt`); the same file has a `combine` over 6+ flows;
  - `Channel` + `receiveAsFlow()` — about 158, for one-time events; collected in Compose through `ObserveOneTimeEvents(flow)` (`core/core_ui/.../compose/util/Flow.kt`, `minActiveState = STARTED`);
  - `combine` — about 47, `flatMapLatest` — 7, `callbackFlow` — 4, `shareIn` — 2;
  - `flowOn(dispatchers.default())` before `stateIn` for heavy UiState mapping.
- WorkManager: `CoroutineWorker` in `component/notification/data/.../push/*Worker.kt`.

## 8. Testing

- **Libraries** (convention plugins + `build.gradle.kts`):
  - JUnit4 (`libs.junit`) — added by every plugin except `<app>.application` / `.compose` / `.detekt` / `.kover`;
  - `kotlinx-coroutines-test` (about 43 modules, by hand);
  - `kotlin("test")` (6);
  - Robolectric (in `<app>.screen` by default, `RobolectricTestRunner` in about 26 test files);
  - `compose-ui-test-junit4` (`<app>.screen`; `createComposeRule` in 3 files);
  - `ktor-client-mock` (1, `core:network:data`);
  - `androidx.navigation.testing` (1);
  - `detekt-test`.

  Assertions are mostly `org.junit.Assert` (about 206 files vs 18 with `kotlin.test`). Turbine is not used.
- **Mocks vs fakes:** hand-written fakes only, **Mockito / MockK are absent from the project**. Shared fakes are extracted into `testFixtures`:
  - `core/coroutines` — `TestCoroutineDispatchers`;
  - `core/core_ui`;
  - `component/<app>-pay/domain-api` — `Fake<App>PayRepository` with a `MutableStateFlow`, `next*Result` and `CompletableDeferred` gates to control timing;
  - `component/filter/domain`;
  - `core:analytics-test`.

  Other fakes are local: `FakeBasketRouter`, `FakeNavigationDispatcher`, `FakeFavoriteRemoteDataSource`.
- **Source sets:** only `src/test` (JVM). `androidTest` — a single file (`shared/product/src/androidTest/.../BasketButtonUiTest.kt`). No `commonTest` / `jvmTest`.
- **Coverage by layer** (number of test files):
  - `screen/` — about 102: basket 14, `<app>-pay` 20, order_history 14, menu 9, scanner 9, qr 8…; most screens have no tests;
  - `component/*/domain` — about 73: basket, checkout value objects, masterpass, phone…;
  - `component/*/data` — about 42: `<app>-pay` 8, notification 6, product 5, basket 4…;
  - `component/*/usecase` — 12 (`<app>-pay`, `<app>-cafe`);
  - `app/` — 22: router impl, deeplinks, analytics pipeline;
  - `shared/` — 12, `detekt-rules` — 30, `core` — about 28.
- **Gate:**
  - `koverVerify` with `minBound(5)` in the root `build.gradle.kts` (modules enter the aggregate through the `<app>.kover` plugin);
  - `scripts/pr-checks.sh` requires tests for NEW files in `component/*/usecase/src/main` (`TEST_REQUIRED_PATHS`).
- **Naming style:** business language in backticks. Example: `` `an empty basket keeps the full screen shimmer until the recommendations answer` `` (`screen/basket/src/test`).
- **Running:** `./gradlew unitTest` — the aggregate of JVM `test` + `testDebugUnitTest`. A bare `testDebugUnitTest` skips the JVM modules (see `AGENTS.md`).

## 9. Code style and conventions

**Naming that actually recurs:**
- `XRepository` / `XRepositoryImpl`
- `XRemoteDataSource` / `XRemoteDataSourceImpl` (package `cloud/`)
- `XLocalDataSource` / `XLocalDataSourceImpl` (package `local/`)
- `XDto`, `XRequest` / `XResponse`
- `XCache` with `EMPTY`
- `XUseCase` (+ `XUseCaseImpl` in legacy): prefixes `Get` / `Observe` / `Fetch` / `Set` / `Clear` / `Add`
- `XViewModel`, `XUiState` (with `EMPTY` / `INITIAL`), `XActions`, `XEvent`, `XMutableState`
- `XRouter` in screen → `XRouterImpl` in `app/mediator/routerImpl`
- UI models with the `Ui` suffix
- Fakes: `FakeX`, `TestX`

**DI modules in app:** `<Name>SingletonModule`, `<Name>ViewModelComponentModule`.

**Packages:** `ua.<app>.android.screen.<name>`, `ua.<app>.android.component.<name>.<layer>`, `ua.<app>.android.core.<name>`.

**Sealed:** `sealed interface` for results (`DomainResult`, `NetworkResult`, `*Result`), states (about 28 `*State`) and events (about 15 `*Event`) with `data object` / `data class` subtypes.

**Static analysis** (wired by the `<app>.detekt` plugin and `configureLint()`):
- `config/detekt/config.yml`: detekt 1.23.8 + `detekt-formatting` + `io.nlopez.compose.rules`. A separate baseline per module (`detekt-baseline*.xml`). `autoCorrect` is on locally and off in CI (`CI=true`).
- Custom rules (module `:detekt-rules`, rule set section in `config.yml`):
  - `NoBackingFieldPattern`, `NoPairTriple`, `NoLocalFunction`, `NoLetForNullCheck`
  - `ExplicitReturnRequired`, `ExpressionBodyOnSameLine`
  - `NoCommentInMethodBody`, `NoBlankLineInMethodBody`, `BlankLineBetweenDeclarations`, `SpaceAfterIfCondition`
  - `NoAndroidLog`
  - `SingleInvokeInUseCase`, `OneClassPerFile`
  - `ActionsFunctionNaming`, `NoFieldInjection`, `NoRepositoryInViewModel`, `NoContextInViewModel`, `NoHardcodedDispatcher`
  - `PublicFunctionBeforePrivate`
  - `NoHardcodedStringInComposable`, `TopLevelComposePropertyPlacement`, `PreviewComposableLast`, `ComposableNoDomainModel`, `PublicComposableInScreenModule`, `UseThemeShapes`, `HardcodedColor`, `MissingComposablePreview`
  - `PreferFunctionReference`
- Android Lint: `config/lint/lint.xml` + per-module `lint-baseline.xml`, `abortOnError = true`.
- Compose stability: `config/compose/stability-config.conf`, `unstable-allowlist.txt`, task `verifyComposeMetrics`.
- `kotlin.code.style=official`, `warningsAsErrors=true`.
- Detailed style rules in `CLAUDE.md` (section Code Style) and `docs/static-analysis.md`.

## 10. Git / CI

- **Commits:** `<TICKET>-<number>. <Message starting with a capital letter>`, e.g. `<TICKET>-1234. Remove hard-coded Dispatchers`.
  - Technical tasks use numbers `0001` / `0002` / `0003`.
  - Merge commits: `Merge branch …` / `Merged in release/x.y.z (pull request #N)`.
  - `AGENTS.md`: **do not add `Co-Authored-By` trailers**.
- **Branches:** `feature/<TICKET>-1234_desc` / `fix/<TICKET>-1234_desc`. Main branches: `master`, `dev`, `release/*`.
- **Git hooks** (`config/git-hooks/`, installed by the `installGitHooks` task):
  - `pre-commit` — `./gradlew detekt` when `.kt`/`.kts` files are staged + `gitleaks`;
  - `pre-push` — the branch name must contain a ticket, and the branch's first commit must start with `<TICKET>-N. `.
- **CI:** Bitbucket Pipelines (`bitbucket-pipelines.yml`). There is no `.github/workflows` or `.gitlab-ci.yml`. Image `ghcr.io/cirruslabs/android-sdk:36`, every Gradle step goes through `scripts/ci-gradle.sh`.
  - On every PR, in parallel:
    - `detekt detektMain detektTest`
    - `lintDebug`
    - `unitTest koverVerify`
    - gitleaks
    - `validateModuleNames validateArchitectureDependencies`
    - `compileDebugSources verifyComposeMetrics` (with `-PenableComposeReports=true`)
    - SCA: CycloneDX SBOM of `:app` + Trivy, fails on CRITICAL
    - `scripts/pr-checks.sh`: branch name / ticket, mandatory tests for new use cases, suppress-baseline
  - On `dev` / `master` — the same gates after merge, except pr-checks.
  - Custom: `nightly-security` (OWASP dependency check), `weekly-dependency-updates`.

## 11. AI Assistant Rules

1. **First determine the component layout** in `settings.gradle.kts`.
   - In 4-module components (`model` / `domain-api` / `usecase` / `data`) write new code as `class XUseCase @Inject constructor` with `suspend operator fun invoke()` + `@Binds` in `component/<name>/data/.../di/`.
   - In legacy `domain` / `data` components follow the local style: `fun interface` / `Impl` + `@Provides` in `app/.../di/component/<Name>ViewModelComponentModule.kt`.
   - Do not migrate the layout in passing.
2. **Every new module goes only through the convention plugin of the matching type:**
   - screen / shared → `<app>.screen`;
   - `model` / `domain-api` → `<app>.jvm.library`;
   - `usecase` → `<app>.component.usecase`;
   - `data` → `<app>.component.data`.

   **Forbidden:**
   - configuring `compileSdk` / `minSdk` / `jvmTarget` / compose / lint / detekt by hand in a module's `build.gradle.kts`;
   - duplicating dependencies the plugin already adds;
   - adding Android/Hilt to JVM modules.

   Changing a plugin affects dozens of modules, so agree on it first. After edits in `build_logic` or `:detekt-rules` stop the daemon (`./gradlew --stop`), otherwise the old version is picked up.
3. **Do not violate `config/architecture/rules.json`:**
   - screen does not depend on `data` or `domain-api`;
   - usecase does not depend on another usecase or on `data`;
   - `core` knows nothing about component / screen / shared.

   Do not add new entries to `exceptions`. The ViewModel works only through use cases (`NoRepositoryInViewModel`), even when a legacy `domain` makes the repository visible.
4. **Networking — only through `<App>HttpClient.safeRequest` / `safeRequestPreservingBody`** with the right qualifier `@AuthorizedClient` / `@UnauthorizedClient`.
   - The RemoteDataSource returns `NetworkResult<ResponseWrapper<Dto>>`.
   - The Repository maps into `DomainResult` or an operation-specific sealed `*Result`.
   - DTOs / `@Serializable` / Ktor types do not leave `data`.
   - If the UI needs to tell a timeout from a server error, do not use `toDomainResult`: it collapses them into `Failure("")`.
5. **Every model lives in its own layer** (see section 6):
   - `*Request` / `*Response` / `*Dto` / `*Cache` with `@Serializable` — only in `component/*/data`;
   - domain models — in `domain` / `model`, without serialization annotations;
   - `*Ui` / `*UiState` (`@Immutable`, `ImmutableList`, ready strings) — only in `screen` / `shared` / `core_ui`;
   - `*Destination` routes — in `screen/*/navigation`.

   **Who maps what:**
   - the Repository builds a Request from domain parameters;
   - the RemoteDataSource does not map — only `BaseRequest("<ApiMethod>", request)` + `safeRequest`;
   - DTO → domain is an extension in `data` (in the new layout — `internal` in `data/.../cloud/mapper/`);
   - domain → UI is a `*DomainToUiMapper` with `StringRetrieval`, or `toUi()` in the UI model file when no resources are needed.

   Money, weight and plurals are formatted only in the UI mapper. New mappers must be covered by a `*MapperTest` with a fake `StringRetrieval`.
6. **Never swallow `CancellationException`.** In every `try/catch` around suspend code with a broad `catch`, put `catch (e: CancellationException) { throw e }` first, as in `<App>HttpClient` and `AuthorizedHttpClient`.
7. **Do not hard-code `Dispatchers.*`, `GlobalScope`, `CoroutineScope(...)`.**
   - Inject `CoroutineDispatchers` and the application `CoroutineScope`.
   - In tests use `TestCoroutineDispatchers` from `testFixtures(projects.core.coroutines)` + `runTest`.
   - The plugins do not add `kotlinx-coroutines-test`; it has to be declared by hand.
8. **Local persistence — DataStore in `*LocalDataSourceImpl`:**
   - a typed serializer;
   - `ReplaceFileCorruptionHandler`;
   - `XCache.EMPTY`.

   Do not add Room / SharedPreferences without agreement — the project has neither.
9. **Build screens to the `<App>PayViewModel` contract:**
   - a single `StateFlow<XUiState>` via `stateIn(…, SharingStarted.whileSubscribed(), XUiState.EMPTY)`;
   - a private `MutableStateFlow<XMutableState>` with `update { it.copy() }`;
   - `Channel` + `receiveAsFlow()` for events, `ObserveOneTimeEvents` in the UI;
   - a `@Stable interface XActions` implemented by the ViewModel;
   - a Router interface in screen, its implementation in `app/mediator/routerImpl`.

   Navigation arguments: in `screen/` via `toRoute()`, in `shared/` via `@AssistedInject`.
10. **Tests:**
   - JVM only (`src/test`), hand-written fakes only, no Mockito / MockK;
   - names are business sentences in backticks;
   - Robolectric only when the Android framework is needed (`toRoute()` / `Bundle`).

   A test in the same PR is mandatory for every new file in `component/*/usecase/src/main`: it is a blocking check in `pr-checks.sh`. Put reusable repository fakes in the `testFixtures` of the `domain-api` module. Run through `./gradlew unitTest`, not `testDebugUnitTest`.
11. **Any warning fails the build** (`warningsAsErrors=true`), and detekt has 30 custom rules.
    - Check `./gradlew detekt` and the relevant `unitTest`.
    - Do not regenerate a baseline to hide a new finding.
    - Do not use `Pair` / `Triple`, `.let` for null checks, comments or blank lines inside method bodies, local functions, `_` backing fields, hard-coded strings and colours in composables.
    - Previews always go last in the file.
    - Git: message `<TICKET>-<N>. <Message>` without `Co-Authored-By`; new modules in kebab-case via `./scripts/create-component.sh`; commit / push only on an explicit user request.
