---
name: add-feature
description: Use when creating a new feature module under feature/ (profile, products-overview, details, cart, categories, category-search, checkout, payment-completed, admin-panel, manage-product) or when replacing a NavigationPlaceholderScreen with a real screen. Covers Gradle wiring, the BaseActionHandleViewModel screen, navEntry, :di assembly and tests. When porting a NutriSport screen, also read nutrisport.md in this skill.
---

# Add a feature module

A feature is **presentation only**. If the ViewModel needs a use case that does not exist yet, stop and build it
first (skill `add-component` for a new entity/process, skill `api-endpoint` for a new server call).

Examples to mirror: `feature/home` (screen that loads data) and `feature/authentication` (form + validator + tests).

## 1. Gradle wiring

1. `build-logic/convention/src/main/kotlin/utils/enums/FeatureName.kt`:
   `PROFILE(dirName = "profile", components = listOf(ComponentName.CUSTOMER))`.
   The plugin wires each listed component's `usecase` + `model`; list only the components whose use cases the
   ViewModel calls.
2. `settings.gradle.kts`: `include(":feature:profile")`. Directory is kebab-case (`products-overview`), the package
   uses `_` (`com.store.feature.products_overview`). Always flat: a sub-screen of another feature (checkout of cart,
   manage-product of admin-panel) is its own `:feature:<name>`, never a module inside that feature.
3. `feature/profile/build.gradle.kts` contains only `plugins { alias(libs.plugins.store.feature.presentation) }`.
   Add a capability plugin only when needed: `store.feature.uiTest` (Compose UI tests in `src/uiTest/kotlin`),
   `store.firebase.auth`, `store.kmpauth.google`.
4. `utils/enums/ModulePath.kt`: `FEATURE_PROFILE(":feature:profile")`; `DiModulePlugin`: `module(ModulePath.FEATURE_PROFILE)`.
5. Tell the user to sync Gradle and run `./gradlew --stop` — the daemon keeps old plugin classes.

## 2. Files — package `com.store.feature.profile`

```
ProfileScreen.kt
ProfileViewModel.kt
ProfileMockPreview.kt            previews use PreviewTheme {}, never BaseTheme
view_data/ProfileViewData.kt     @Immutable data class; defaults are the initial state; ImmutableList for lists
view_data/ProfileViewAction.kt   sealed interface ProfileViewAction : ViewAction — data object / data class only
view_data/ProfileInitializer.kt  ViewDataInitializer.Scoped<ProfileViewData>, when the screen loads data on open
validator/ProfileValidator.kt    only for forms: SuspendFieldValidator.Abstract<ProfileViewData> + FieldKey
di/ProfilePresentationModule.kt
```

## 3. ViewModel

```kotlin
class ProfileViewModel(
    initializer: ProfileInitializer,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    dispatchers: AppDispatchers,
) : BaseActionHandleViewModel<ProfileViewData>(dispatchers) {
    override val _viewData = MutableStateFlow(ProfileViewData())

    init {
        initializer.initialize(this)
    }

    override suspend fun handleViewAction(action: ViewAction) {
        when (action) {
            ProfileViewAction.UpdateClicked -> handleUpdateClicked()
        }
    }
}
```

- One dispatch point: `handleViewAction` with a `when` that calls private handlers. Never block it — use `launchIo { }`.
- Use-case results: `.onSuccess { }` / `.onError { }` on `ApiResult`. State changes only via `updateViewData { copy(...) }`.
- Navigation: `navigate(Screen.X)` / `navigateInclusive(Screen.X)`. Messages: `showError(...)` / `showMessage(...)`.
- Text comes from Compose resources (`getString(Res.string.…)`, `com.store.core.resources`), never hardcoded.
- Map the component error (`CustomerError`, `ProductError`, …) to text here, one `when` per error type, as
  `SignInFailureHandler` does. There is no shared `DataError.toUiText()` yet.
- Form input: `is InputFieldChanged -> validator.validateAdvanced(action, scopeFor(_viewData))` (see `AuthenticationViewModel`).
- Money totals and multi-entity writes are never computed here — they come from the server.

## 4. Navigation and DI

- Screen keys live in `core/presentation/.../navigation/Screen.kt`; most already exist (`Profile`, `Cart`, `Details(id)`,
  `CategorySearch(category)`, `ManageProduct(id)`, `Checkout`, `PaymentCompleted`). Arguments go into the key —
  never `SavedStateHandle`. A new key is a `@Serializable data object` / `data class` implementing `Screen`.
- `di/ProfilePresentationModule.kt` registers only what the feature owns (no use cases):
  ```kotlin
  @OptIn(KoinExperimentalAPI::class)
  val profilePresentationModule = module {
      factoryOf(::ProfileInitializer)
      factoryOf(::ProfileValidator)
      viewModelOf(::ProfileViewModel)
      navEntry(Screen.Profile.serializer()) { ProfileScreen() }
  }
  ```
- **Delete the placeholder** `navEntry(Screen.Profile.serializer())` from `di/src/commonMain/.../modules/AppNavigationModule.kt`,
  otherwise one key has two entries.
- `:di`: `di/src/commonMain/kotlin/com/store/di/modules/profile/ProfileFeatureModule.kt` →
  `val profileFeatureModule = module { includes(profilePresentationModule) }`, then add it to `featureModules` in `CoreModules.kt`.

## 5. Tests — `feature/profile/src/commonTest`

- `ProfileViewModelTest : BaseViewModelTest()`, every test is `= runVmTest { }`.
- Real use cases over the repository fakes from `:test` (`FakeCustomerRepository`, `FakeProductRepository`, …);
  set their `*Result` fields, use `gate` to hold a call in flight when testing loading state.
- Test names are snake_case sentences (`update_shows_error_when_customer_not_found`) — D8 rejects spaces on `androidDeviceTest`.
- Cover: initial state, each `ViewAction`, success and each error branch of every use case.

## 6. Before handing back

- No `android.*` in `commonMain` (a hook blocks it), no Ktor/HTTP, no repository, no `Dispatchers.*` in the feature.
- Update `CLAUDE.md` "Current wiring" if a placeholder became a real screen.
- Do not run Gradle. Ask the user to run:
  `./gradlew :feature:profile:jvmTest :di:jvmTest validateArchitectureDependencies`
