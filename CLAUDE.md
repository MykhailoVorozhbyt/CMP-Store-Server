# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Kotlin Multiplatform (KMP) e-commerce application with a Ktor backend, targeting Android, iOS, Desktop (JVM), and Server. 
Two store variants (AthleticaPlus, NutriSport) share all logic but have their own branding.

The Ktor server owns identity and persistence (SQLite + opaque session tokens). The client keeps a Room cache of the
signed-in customer in `:core:data`; the server stays the source of truth and every screen refreshes from it. 
Firebase is **not** the source of truth anymore — it survives only as the mobile Google Sign-In provider (via KMP-Auth). A session exists only after a successful
`/auth/authorize`; a Firebase user alone never counts as signed in, and a failed authorize signs Firebase out again.

Client code is split into `component/<entity>/` (business logic) and `feature/<name>` (UI only) — see
"App Architecture". The split is enforced by `./gradlew validateArchitectureDependencies`.

## Build & Run Commands

All commands use the Gradle wrapper. On Windows use `.\gradlew.bat` instead of `./gradlew`. JVM toolchain is **Java 21**.

```bash
# Ktor server (port 8080)
./gradlew :server:run
./gradlew :server:runFatJar        # preferred while a desktop client is also running

# Desktop (JVM) apps — the store modules are KMP libraries; the apps live in :desktopApp / :androidApp
./gradlew :app:athletica-plus:desktopApp:run
./gradlew :app:nutri-sport:desktopApp:run

# Android APKs
./gradlew :app:athletica-plus:androidApp:assembleDebug
./gradlew :app:nutri-sport:androidApp:installDebug

# Tests — same split as CI
./gradlew :server:test
./gradlew allTests -x :server:test  # every KMP module (jvm + androidHost); iOS only on macOS
./gradlew validateArchitectureDependencies  # module-dependency rules; run by NAME (configure-on-demand)
./gradlew validateSessionScopedStores       # every *LocalDataSource is wiped on sign-out or listed as device-scoped
./gradlew :feature:authentication:jvmTest      # incl. headless Compose UI tests
./gradlew :feature:authentication:androidDeviceTest  # same UI tests on emulator
```

For iOS: open `app/iosApp` in Xcode and run from there. The Xcode project was moved there without being rebuilt on a
Mac — see Known Gaps.

The desktop app reads `DESKTOP_CLIENT_SECRET` at startup (env var or repo-root `secrets.properties`); without it Koin
fails while building `GoogleSignInService`, even for manual email sign-in. A Gradle daemon started from a terminal uses
the first `java` on `PATH` because `gradle/gradle-daemon-jvm.properties` has not been generated
(`./gradlew updateDaemonJvm`).

**Gotcha:** running a desktop store app rebuilds `shared-jvm.jar`, which clobbers the jar a live `:server:run` has loaded → `ClassNotFoundException`, usually surfacing as a `400 SERIALIZATION` response. Run the server via `runFatJar` or `installDist` when working on both sides at once.

## Conventions

Always loaded:

@.claude/conventions/code-style.md
@.claude/conventions/architecture.md
@.claude/conventions/di.md

Path-scoped rules in `.claude/rules/` load only when matching files are touched: `build-logic`, `presentation` (BaseActionHandleViewModel, StoreTheme, Navigation3), `server`, `client-auth`, `testing`, `versions`.

Procedures are skills in `.claude/skills/`: `add-feature` (incl. porting NutriSport screens), `add-component`,
`api-endpoint`, `verify-change` (run it before calling a change done). Read-only agents in `.claude/agents/`:
`platform-parity` (expect/actual coverage for android/ios/jvm), `code-improver` (review on request).

## Known Gaps (as of 2026-09-29)

- The Room database (`:core:data`) is not encrypted: the cached customer (address, phone) sits in plain SQLite,
  protected only by the app sandbox (and `~/.store_app` on desktop) and wiped on sign-out.
- The Room schema is at version 1 with destructive migration; there are no migrations yet.
- `feature:home:presentation` has no tests; `HomeGraphInitializer` (customer → cart products → total) is only checked by running the app.
- No shared `DataError.toUiText()` yet; the home screen shows one generic text per failing call.
- Without a connectivity service, `ConnectException` (e.g. the dev server is down) is reported as `NO_INTERNET`.
- `safeApiCall`'s status fallback still maps a bare 409 to the auth-specific `USER_ALREADY_EXISTS`.
- The iOS actual `PlatformNetworkError.ios.kt` is only compiled by an Xcode / macOS build — `allTests` on Windows skips it.
- `app/iosApp/iosApp.xcodeproj/project.pbxproj` was edited on Windows during the move to `app/` and has not been built
  since: the build-phase scripts (`cd "$SRCROOT/../.."`, `:app:<store>:embedAndSignAppleFrameworkForXcode`) and
  `FRAMEWORK_SEARCH_PATHS` (`$(SRCROOT)/../<store>/build/xcode-frameworks/…`) need a first run in Xcode. The AthleticaPlus
  framework phase has `buildActionMask = 12` (NutriSport: `2147483647`), and NutriSport has an empty `ShellScript` phase.
- iOS bundle IDs (`org.cmp.store.<store>`) do not follow the Android `applicationId` scheme (`com.store.<store>`).
- `Screen.ManageProduct` and `Screen.PaymentCompleted` have no `navEntry` — navigating to them fails.
- `Screen.ContactUs`'s placeholder is mislabelled `"Products overview"` in `di/modules/AppNavigationModule.kt`.
- Server has no CORS and no CallLogging; SQLite is a single dev file with no migrations (schema is recreated, not migrated).
- A future authed "set password" endpoint (letting Google users add a MANUAL credential) is designed but not built — it can now hang off the profile screen.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).
