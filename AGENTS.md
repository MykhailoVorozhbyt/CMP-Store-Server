# AGENTS.md

Kotlin/Compose multiplatform multi-module app (Koin, Jetpack Navigation). 
Full architecture, code style, and testing philosophy live in `CLAUDE.md` (auto-loaded).
This file adds only the facts those miss.

## Commands

- **Run Athletica-Plus Desktop (JVM) app:** `./gradlew :app:athletica-plus:desktopApp:run`.
- **Run Nutri-Sport Desktop (JVM) app:** `./gradlew :app:nutri-sport:desktopApp:run`.
- **Run Athletica-Plus Android app:** `./gradlew :app:athletica-plus:androidApp:assembleDebug`.
- **Run Nutri-Sport Android app:** `./gradlew :app:nutri-sport:androidApp:assembleDebug`.
- **Run server:** `./gradlew :server:run`; **test server:** `./gradlew :server:test`.

## Architecture

Full detail in `CLAUDE.md`; this is what differs from filenames:

- **Two component layouts coexist.** Newer components use 4 modules (`model`/`domain-api`/`usecase`/`data`); Check `settings.gradle.kts` before assuming a layout.
- Screens depend on `usecase` + `model` only — never `domain-api` or repositories directly.
- Data flow: Screen → ViewModel → UseCase → Repository → DataSource.
- New modules must be kebab-case.
- Namespaces `com.store.*`.
- Git: branch `feature/STORE-<N>-<APP/SEVER?_desc` / `fix/STORE-<N>-<APP/SEVER>_desc`; commit `STORE-<N>-<APP/SEVER>. Short message`.
