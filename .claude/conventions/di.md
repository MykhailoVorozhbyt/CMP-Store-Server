## Dependency Injection (Koin)

`initializeKoin(vararg appModules, config)` in `:di` (`KoinInit.kt`) starts Koin with `sharedAppModule + platformModule + appModules`.

```
sharedAppModule
├── coreModule            # platformModule (expect/actual, currently empty) +
│                         #   dispatchersModule + networkModule + secureStorageModule + databaseModule
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
- `databaseModule` — owned by `core:data` (`com.store.core.data.di`): the `StoreDatabase` (BundledSQLiteDriver, IO
  query context, destructive migration — it is a cache) and every DAO. The platform `RoomDatabase.Builder` comes from expect/actual `databaseBuilderModule`
  (Android `getDatabasePath`, iOS Documents, JVM `~/.store_app/store.db`).
- `LocalAuthSessionDataSource` is built with `getAll<SessionScopedStorage>()`. A store with per-user data implements
  `SessionScopedStorage` and binds it next to its own binding
  (`singleOf(::RoomXLocalDataSource).binds(listOf(XLocalDataSource::class, SessionScopedStorage::class))`) —
  never through a central list. `validateSessionScopedStores` enforces it.

`SERVER_BASE_URL` is expect/actual in `core/network/.../ServerUrl.kt`. **The Android actual is a hardcoded LAN IP** — change `ServerUrl.android.kt` (and the IP whitelisted in each store's `network_security_config.xml`) when the dev machine's IP changes, or switch it to `10.0.2.2` for the emulator.

