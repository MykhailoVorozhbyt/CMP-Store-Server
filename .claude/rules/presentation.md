---
paths:
  - "feature/**"
  - "core/presentation/**"
  - "core/navigation/**"
  - "app/**"
---

## BaseActionHandleViewModel Pattern

All ViewModels extend `BaseActionHandleViewModel<VD>` where `VD` is the ViewData (UI state holder). 
Constructor takes `dispatchers: AppDispatchers`.

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

## Previews

Every `@Composable` function has at least one preview of its own, `private` and in the same file, wrapped in `PreviewTheme {}`.
Components use `@Preview`; screen-sized content uses `@PhonePreview` (or another `AdaptivePreview` annotation) with a
`PreviewProvider` when it has several states. A preview that needs a background sets it itself
(`Modifier.background(StoreTheme.color.surface)`) — `PreviewTheme` draws none.
The only exception is the Koin entry point (`XScreen(viewModel = koinViewModel())`): its `XContent` carries the previews.
Enforced in `feature/profile` today; other modules are brought in line when touched.

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

## Navigation (Navigation3)

Uses Jetpack Navigation3 (multiplatform). `Screen` is a `sealed interface Screen : NavKey` in `core/presentation/.../navigation/Screen.kt` (not in `:shared`).

Assembly, all in `:core:navigation`:
- `di/NavModuleExt.kt` — the `navEntry(serializer) { key -> Composable }` and `navKey(serializer)` Koin DSL. Registering an entry also registers a `NavKeyProviderInstaller`.
- `di/RememberNavBackStack.kt` — `rememberKoinNavBackStack` builds a polymorphic `SavedStateConfiguration` from every registered installer. This is why each screen must be registered with its own `serializer()` — KMP has no reflective polymorphic serialization.
- `NavigationState` / `RootNavigator` — a top-level stack plus per-tab sub-stacks; reselecting a tab clears its sub-stack.
- `AppNavigator` (`navigate` / `replaceAll` / `back`), reachable in Composables via `LocalAppNavigator`.
- `NavGraph.kt` — `SetupNavGraph(startDestination)` → `NavDisplay` with `koinEntryProvider()`.

**Adding a screen:** add the object/data class to `Screen.kt`, then `navEntry(Screen.Foo.serializer()) { FooScreen() }` inside the owning feature's presentation Koin module. A whole new feature: skill `add-feature`.

Current wiring: `Screen.Auth`, `Screen.HomeGraph` and `Screen.Profile` are real screens. Eight more (`ProductsOverview`, `Cart`, `Categories`, `CategorySearch`, `AdminPanel`, `ContactUs`, `Details`, `Checkout`) are registered as `NavigationPlaceholderScreen` in `di/modules/AppNavigationModule.kt`. `ManageProduct` and `PaymentCompleted` are **not registered at all** and will fail at runtime if navigated to.

