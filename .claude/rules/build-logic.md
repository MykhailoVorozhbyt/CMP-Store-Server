---
paths:
  - "build-logic/**"
  - "**/*.gradle.kts"
  - "config/architecture/**"
---

## Convention Plugins (build-logic)

**Never configure a module's `build.gradle.kts` manually.** Every module's build file contains only a `plugins { alias(...) }` block.

**Plugin families:**
- **Layer plugins** — `store.component.model` / `.domainApi` / `.usecase` / `.data` and one `store.feature.presentation`.
  They derive the namespace from the path, auto-wire the sibling layers of the same component (via `project.parent`),
  and fail the build if the path is not `:component:<name>:<layer>` / `:feature:<name>` or `<name>` is
  missing from `ComponentName` / `FeatureName`. Component layers get no Compose and no Android resources.
- **Capability plugins** — stacked next to a layer plugin for extras: `store.firebase.auth`, `store.kmpauth.google`,
  `store.feature.uiTest`. They wait for KMP (`withPlugin`), so their order in `plugins { }` doesn't matter.
- **Per-module plugins** for everything else (`core:*`, `:shared`, `di`, `test`, `server`) and the root-only
  `store.architecture`.
- **App plugins** (`plugins/app/`) — `store.app.shared` for `:app:shared`, and per store
  `store.app.<store>.library` / `.androidApp` / `.desktopApp` (`StoreModulePlugin`, `StoreAndroidAppPlugin`,
  `StoreDesktopAppPlugin`, one subclass per store). Names visible to code are pinned in `StoreModulePlugin` so moving a
  module never changes them: the iOS framework `baseName` (Swift `import StoresAthletica_plus`) and
  `packageOfResClass` (`com.store.<store>.resources`). Left to defaults, both are derived from the Gradle path.

**Adding a component or a feature:** follow the `add-component` / `add-feature` skill. Both touch `ComponentName` /
`FeatureName`, `settings.gradle.kts`, `DiModulePlugin`; a component also touches `TestModulePlugin` and `rules.json`.

**Enums** in `build-logic/convention/src/main/kotlin/utils/enums/` — never raw strings in plugins:
- `ComponentName`, `ComponentLayer`, `FeatureName` — see above; `component(name, layer)` builds a typed path
  (`module(component(...))` / `apiModule(component(...))`).
- `ModulePath` — Gradle paths of non-component modules. The `:androidApp` / `:desktopApp` submodules have no entries.
- `ModuleName` — only namespaces that do NOT follow the path rule (`:shared`, `:app:shared` = `org.cmp.store`, stores,
  core modules).
  Component and feature namespaces are derived (`extensions/NamespaceExtensions.kt`).
- `BuildTypeName` — `debug` / `release`.

Dependency aliases and plugin IDs come from the version catalog through the accessors in `extensions/ProjectExtensions.kt` (`libs.*`, `pluginManager.alias(libs.plugins.*)`) — there is no `LibraryName` or `PluginName` enum.

Shared helpers live alongside the plugins:
- `configuration/AndroidBase.kt` — `configureAndroidLibraryBase(namespace, enableAndroidResources = true)` (SDK levels, host/device test trees)
- `configuration/PureKmp.kt` — `configurePureKmpLibrary()` for component layers (android + iOS + jvm, no Compose/resources)
- `configuration/IOS.kt` — `configureIOS(frameworkName = null)` → iosArm64 + iosSimulatorArm64; a static framework
  only when a name is passed (just the two store libraries — Xcode links nothing else)
- `configuration/ComposeDesktopApplication.kt` — Dmg/Msi/Deb packaging, applied by `StoreDesktopAppPlugin`
- `extensions/DependencyExtensions.kt` — `module(ModulePath.X)`, `implementation`, `testImplementation`
- `extensions/ComponentExtensions.kt` — `component()`, `module()/apiModule(ComponentModule)`, `owningComponent()`
- `extensions/SourceSetExtensions.kt` — `sourceSet(name, srcDir)`
- `architecture/` — `ArchitectureRules` + `ValidateArchitectureDependenciesTask` (see "Enforcement" in `.claude/conventions/architecture.md`)
- `utils/Java.kt` — Java 21 / `JvmTarget.JVM_21`

After editing anything in `build-logic`, sync and run `./gradlew --stop` — the daemon keeps the old plugin classes.

