---
name: add-component
description: Use when adding a new business component under component/ (e.g. basket, order, payment) — the four layers model / domain-api / usecase / data, its error type, Koin modules, the :test fake and the architecture rules. Not for adding a use case to an existing component (see api-endpoint).
---

# Add a component

Mirror `component/customer` — the smallest complete example. Placeholders below use `basket`.

## 0. Entity or process?

- **Entity** (`customer`, `product`, `auth`): owns one kind of data. May depend only on another component's `model`.
- **Process** (`basket`, `order`, `payment`): its `usecase` may read another component's `domain-api` — **reads only**.
- Writes that span several entities (order + clear basket) and money totals are one server call, not client orchestration.
- Never: `data` → another `data`, `usecase` → another `usecase`, cycles. Admin is a role, not a component.

## 1. Gradle wiring

1. `build-logic/convention/src/main/kotlin/utils/enums/ComponentName.kt`: `BASKET("basket")`.
2. `settings.gradle.kts`: include `:component:basket:model`, `:domain-api`, `:usecase`, `:data`.
3. One-line `build.gradle.kts` per layer: `alias(libs.plugins.store.component.model)` / `.domainApi` / `.usecase` / `.data`.
4. `DiModulePlugin`: `module(component(ComponentName.BASKET, ComponentLayer.DATA))` and `… ComponentLayer.USECASE`.
5. `TestModulePlugin`: `apiModule(component(ComponentName.BASKET, ComponentLayer.DOMAIN_API))` — otherwise the fake in `:test` cannot see the repository.
6. Process component reading another `domain-api`: add it to the `(?!(basket|order|payment):)` lookahead in
   `config/architecture/rules.json` (already contains these three).
7. Tell the user to sync and run `./gradlew --stop`.

## 2. Layers — package `com.store.component.basket.<layer>` (`domain-api` → `domain_api`)

**model** — `commonMain` only, no Compose, no Android:
```kotlin
sealed interface BasketError {
    data object ItemNotFound : BasketError
    data class Common(val error: DataError) : BasketError
}
```
Domain types that already exist in `:shared` are re-exported with `api(...)`, not duplicated.

**domain-api** — the repository interface. One-shot calls are `suspend fun …: ApiResult<T, BasketError>` /
`EmptyResult<BasketError>`. `Flow` only for data that really changes over time, named `observeX()`.

**usecase** — one class per operation, no base interface:
```kotlin
class UpdateBasketItemQuantityUseCase(private val repository: BasketRepository) {
    suspend operator fun invoke(itemId: String, quantity: Int): EmptyResult<BasketError> =
        repository.updateQuantity(itemId, quantity)
}
```
`usecase/di/BasketUseCaseModule.kt`: `val basketUseCaseModule = module { factoryOf(::UpdateBasketItemQuantityUseCase) }`.

**data** — everything here is `internal` except the Koin module:
- `data_source/BasketDataSource.kt`: `internal interface` + `internal class KtorBasketDataSource(client: HttpClient)`;
  each call is `safeApiCall { client.get(...) }` → `ApiResult<Dto, NetworkError>`. Paths are `private const val`.
- `dto/` — `@Serializable` DTOs; they never leave `data`, even when another component has a similar one.
- `mapper/BasketMappers.kt` — DTO ↔ domain. `mapper/BasketErrorMapper.kt`:
  ```kotlin
  internal fun NetworkError.toBasketError(): BasketError = when (this) {
      NetworkError.BASKET_ITEM_NOT_FOUND -> BasketError.ItemNotFound
      else -> BasketError.Common(toDataError())
  }
  ```
  A decision that needs the exact wire code is taken before mapping — the mapper deliberately loses detail.
- `DefaultBasketRepository` — gets the user id from `LocalAuthSessionDataSource` (never stores its own session),
  maps with `.mapSuccess { }` / `.mapError { it.toBasketError() }`.
- `di/BasketDataModule.kt`: `singleOf(::KtorBasketDataSource).bind<BasketDataSource>()`,
  `singleOf(::DefaultBasketRepository).bind<BasketRepository>()`.

## 3. :di

`di/src/commonMain/kotlin/com/store/di/modules/basket/BasketComponentModule.kt`:
`val basketComponentModule = module { includes(basketDataModule, basketUseCaseModule) }`, then add it to
`componentModules` in `CoreModules.kt`.

## 4. Tests

- `:test` → `test/src/commonMain/kotlin/com/store/test/fakes/FakeBasketRepository.kt`: implements `BasketRepository`,
  a settable `*Result` per method (default: an error), recorded calls, optional `gate` like `FakeAuthRepository`.
- `data/src/commonTest`: `FakeBasketDataSource` (internal type, stays here), `KtorBasketDataSourceTest` with
  `testHttpClient` / `respondJson` from `com.store.test.network`, `DefaultBasketRepositoryTest`,
  `BasketErrorMapperTest` (specific codes, bare `NOT_FOUND`, delegation to `DataError`).
- `usecase/src/commonTest`: `BasketUseCasesTest` over `FakeBasketRepository`.
- snake_case test names, no spaces.

## 5. Before handing back

- The server side exists or is built in the same change (skill `api-endpoint`).
- Update `CLAUDE.md`: module count, "Module Structure", the `componentModules` tree, "Planned (not built yet)".
- Do not run Gradle. Ask the user to run:
  `./gradlew :component:basket:data:jvmTest :component:basket:usecase:jvmTest :di:jvmTest validateArchitectureDependencies`
