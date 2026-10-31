---
name: api-endpoint
description: Use when adding or changing a server route or a client call to it — Ktor route, DAO, error code in NetworkError, client data source, DTO, error mapper, use case and tests on both sides. Use for cart, order, payment, product search and admin product routes. Not for UI-only changes.
---

# Add or change an API endpoint

One logical change touches the server, `:shared` and a client component, in this order. The wire contract is
`:shared` (`org.cmp.store`) — the server depends on it, so it never gets UI or client code.

## 1. Contract

- Request/response bodies: server DTO in `server/.../features/<domain>/dto/`, client DTO in
  `component/<x>/data/dto/`. They are separate copies on purpose. Product routes are the exception: they
  serialize `Product` from `:shared` directly.
- Change DTOs additively: new fields get defaults, so an installed older client keeps working after the server deploys.
- New error code → `shared/src/commonMain/kotlin/org/cmp/store/network/NetworkError.kt` **and** a branch in the
  exhaustive `NetworkError.toDataError()` (`core/network/.../utils/DataErrorMapper.kt`, no `else` there by design).

## 2. Server — `server/src/main/kotlin/org/cmp/store/`

1. **Table** (new data only): `database/tables/XTable.kt` and add it to `SchemaUtils.create(...)` in
   `database/DatabaseFactory.kt`. There are no migrations; the dev schema is recreated.
2. **DAO**: `database/dao/XDao` + `XDaoImpl`, registered in `di/DaoModule.kt`. Public methods wrap work in
   `dbQuery { }`. Methods that must join an outer transaction are non-suspend and named `…WithinTransaction`.
3. **Service** only when one request writes several tables (order + clear basket, money totals): `features/<domain>/service/`,
   one `dbQuery { }` around all `…WithinTransaction` calls, registered in `di/ServiceModule.kt`. A single-DAO
   route calls the DAO directly (see `CustomerRoutes`).
4. **Resource**: typed `@Resource` class in `utils/RoutResources.kt` (nested under its parent, as `Products.ByIds`).
5. **Route**: `features/<domain>/routes/XRoutes.kt` → `fun Route.xRoutes()`, registered in `plugins/Routing.kt`.
   - Auth: wrap in `authenticate(AUTH_TOKEN_PROVIDER) { }`; customer data also needs an own-customer check
     like `requireOwnCustomer` (a valid token alone is not enough).
   - Admin routes: check the caller's `is_admin` on the server. No helper exists yet — add `requireAdmin()` next to
     `requireOwnCustomer` with the first admin route. `is_admin` is never writable through the API.
   - Errors: `throw ApiException(HttpStatusCode.X, NetworkError.Y)`. Never `call.respond(status, "free text")` —
     the client cannot map it.
   - Rate limit: global by default. A `RateLimitGroup` bucket is keyed by (name, request key), not by route —
     reusing a name means sharing its quota.
6. **Tests** (`server/src/test/kotlin/org/cmp/store/`): `features/XRoutesIntegrationTest.kt` with
   `testServerApplication { }`, `seedCustomer`, `customerFixture`, `grantAdmin`. Cover success, 401 without
   token, 403 for another customer / non-admin, each `NetworkError` the route throws. DAO logic → `dao/XDaoTest.kt`
   with `testDaoDatabase { }`.
7. Update the routes table in `.claude/rules/server.md`.

## 3. Client — `component/<x>/`

1. `data/data_source/`: method on the internal `XDataSource` + `KtorXDataSource` (`safeApiCall { client.post(PATH) { setBody(dto) } }`).
2. `data/dto/` + `data/mapper/`: DTO ↔ domain; new specific code → branch in `toXError()`.
3. `domain-api`: repository method; `data`: implementation in `DefaultXRepository` (user id from
   `LocalAuthSessionDataSource`, `.mapSuccess` / `.mapError { it.toXError() }`).
4. `usecase`: `class DoXUseCase(repository) { suspend operator fun invoke(...) }` + `factoryOf` in `XUseCaseModule`.
5. `:test`: add the method to `FakeXRepository`.
6. Tests: data-source test (`testHttpClient` + `respondJson`, including the error body), repository test,
   error-mapper test for the new code, use-case test.

## 4. Before handing back

- The client never computes totals or orchestrates multi-entity writes.
- Do not run Gradle. Ask the user to run:
  `./gradlew :server:test :component:<x>:data:jvmTest :component:<x>:usecase:jvmTest`
- When running the server and a desktop client together, use `./gradlew :server:runFatJar` — a desktop launch
  rebuilds `shared-jvm.jar` under `:server:run` and fakes `400 SERIALIZATION`.
