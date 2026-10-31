---
paths:
  - "test/**"
  - "**/src/*Test/**"
  - "**/src/uiTest/**"
  - "server/src/test/**"
---

## Testing

`:test` is a fixture module whose sources live in **`commonMain`** (not a test source set), so other modules consume it as a normal dependency via `module(ModulePath.TEST)` in their `commonTest`. 
It provides `BaseViewModelTest` / `runVmTest` (a `StandardTestDispatcher` wired into `AppDispatchers` plus `Dispatchers.setMain`) and the fakes (KMP has no `testFixtures`, so they live here): `FakeAuthRepository` (with a `gate`), `FakeCustomerRepository`, `FakeProductRepository` — each implementing its component's `domain-api` — and `FakeLocalAuthSessionDataSource`. Data-source fakes (`internal` types) stay in each component's `data/src/commonTest`. `com.store.test.network` holds the shared Ktor mock helpers (`testHttpClient`, `respondJson`, `jsonHeaders`) used by every data-source test. Its convention plugin re-exports kotlin-test, coroutines-test, Turbine, koin-test, compose-ui-test and the Ktor mock/serialization libraries as `api`.

Compose UI tests live in `feature/<name>/src/uiTest/kotlin/` of any feature that applies the `store.feature.uiTest` capability plugin. 
The plugin wires that directory by `kotlin.srcDir` into **both** `jvmTest` (headless) and `androidDeviceTest` (emulator) — not via `dependsOn`, which is forbidden across source-set trees.

Covered today: `BaseActionHandleViewModel`, string utils, `HttpClientFactory` + `SessionRefreshCall`, `safeApiCall` + error mappers, every component (repositories, data sources, use cases), `AuthenticationViewModel`, the desktop OAuth stack, the Koin graph (`di/src/jvmTest/KoinGraphTest` — `sharedAppModule.verify()`, JVM-only because verify uses reflection), and the whole server (113 tests). 

Untested: `core:security`, `core:navigation`, `core:utils`, `feature:home:presentation`.

`KoinGraphTest` only checks constructor-reference definitions (`singleOf`/`factoryOf`/`viewModelOf`); lambdas like `single { … }` are opaque to it. If it fails on a type that is supplied at runtime (e.g. by the store's theme module), pass it via `verify(extraTypes = listOf(...))` instead of registering a dummy. Same for factory-function definitions: `singleOf(::createHttpClient)` makes verify inspect `HttpClient`'s own constructor, hence `HttpClientEngine` + `HttpClientConfig` in `extraTypes`.

