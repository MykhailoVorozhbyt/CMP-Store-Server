# Project Overview (updated 2026-04-24)

## What This Project Is

**CMP-Store-Server** — Kotlin Multiplatform e-commerce app targeting Android, iOS, Desktop (JVM) + Ktor backend.
Two store variants (`athletica-plus`, `nutri-sport`) share all logic, differ only in theme.
Learning project based on Udemy NutriSport course, but with a custom Ktor backend replacing Firebase.

---

## Module Structure

```
composeApp/          # App entry points per platform + AppViewModel (start destination logic)
server/              # Ktor server — port 8080, Exposed DSL + SQLite
shared/              # Domain models, Screen nav keys, HttpClientFactory, ApiResult, NetworkError, SafeApiCall
stores/
  athletica-plus/    # Theme only (colors, strings) + google-services.json
  nutri-sport/       # Theme only (colors, strings) + google-services.json
feature/
  authentication/
    data/            # RemoteDataSource (Ktor), CustomerRepositoryImpl (Firebase Auth + Ktor)
    domain/          # CustomerRepository interface, use cases (all implemented)
    presentation/    # AuthenticationScreen, HomeScreen, AuthenticationViewModel
core/
  presentation/      # BaseActionHandleViewModel, StoreTheme, UiEvent, ViewAction, StoreSnackbar
  navigation/        # SetupNavGraph (NavDisplay + Koin Navigation3), navEntry DSL
  utils/             # Logger (expect/actual), AdaptivePreview, Alpha
  resources/         # Shared Compose resources (strings, drawables)
di/                  # Koin modules: networkModule, repositoryModule, useCaseModule,
                     # dispatchersModule, viewModelModule, navigationModule
build-logic/         # Convention plugins — NEVER configure modules manually
gradle/              # libs.versions.toml — single source of truth for all versions
```

---

## Server (Ktor)

- Routes: `GET /`, `POST /customer`, `GET /customer/{id}`, `PUT /customer`
- Storage: **Exposed DSL + SQLite** (`store.db`). In-memory `CustomerStorage` deleted.
- Tables: `CustomerTable`, `PhoneNumberTable` (1-to-1), `CartItemTable` (1-to-many)
- `CustomerDao` — `exists`, `create`, `read`, `update` with `newSuspendedTransaction`
- `DatabaseFactory.init()` called at startup — creates tables if missing
- ⚠️ `CustomerDao.mapFrom()` persists `isAdmin` from request body — no server-side protection yet

---

## Network Layer (shared)

- `ApiResult<D, E>` — custom sealed interface replacing kotlin stdlib `Result`
- `EmptyResult<E>` — typealias for `ApiResult<Unit, E>`
- `safeApiCall {}` — centralizes all Ktor exception → `NetworkError` mapping
- `NetworkError` — fully expanded enum: USER_ALREADY_EXISTS, CUSTOMER_NOT_FOUND, REQUEST_TIMEOUT, UNAUTHORIZED, TOO_MANY_REQUESTS, PAYLOAD_TOO_LARGE, SERVER_ERROR, SERIALIZATION, UNKNOWN
- `RemoteDataSource` (renamed from `CustomerApi`) — all 3 methods use `safeApiCall`
- No DTO layer — `Customer` domain model used directly on the wire

---

## Authentication Feature

- Google Sign-In via KMPAuth (`GoogleButtonUiContainerFirebase`) + Firebase Auth
- `CreateCustomerUseCase` — implemented, delegates to `repository.createCustomer(user)`
- `CustomerRepositoryImpl` — uses `ApiResult` pattern, no try/catch; maps to kotlin `Result` for interface compatibility
- `AuthenticationViewModel` — fully wired: calls use case → navigates to Home with welcome message

---

## Navigation (Koin Navigation3 — COMPLETE)

- `navEntry` DSL + `rememberKoinNavBackStack` + `Navigator` (lateinit backStack) + `koinEntryProvider()`
- `Screen.HomeGraph` — `data class(welcomeMessage: String? = null)` to carry post-login message
- `HomeScreen` shows welcome snackbar via `LaunchedEffect(welcomeMessage)` using `MessageEventData.success()`
- Only `Screen.Auth` and `Screen.HomeGraph` wired. All other screens defined but not registered.

---

## DI Modules

All registered in `initializeKoin()`:
- `networkModule` — `HttpClient`, `RemoteDataSource`
- `repositoryModule` — `CustomerRepository` → `CustomerRepositoryImpl`
- `useCaseModule` — all 5 use cases
- `dispatchersModule` — coroutine dispatchers with qualifiers
- `viewModelModule` — `AuthenticationViewModel`, `AppViewModel`
- `navigationModule` — `Navigator`, navEntry for Auth + HomeGraph

---

## Android Networking

- Real device: `http://192.168.0.120:8080` (LAN IP in `ServerUrl.android.kt`)
- `network_security_config.xml` whitelists `192.168.0.120`
- Server must run via `./gradlew :server:run` (binds to `0.0.0.0`, not just localhost)
- Emulator: `http://10.0.2.2:8080`

---

## What Still Needs Doing

- Fix `isAdmin` security: server should ignore client-supplied `isAdmin` on create/update
- Register remaining screens in NavGraph (Products, Cart, Profile, etc.)
- HomeScreen is a placeholder — real implementation needed
- No JWT/auth protection on server routes
- Product, Order, AdminRepository — not started
- PayPal payment integration — not started