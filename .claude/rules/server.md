---
paths:
  - "server/**"
---

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

