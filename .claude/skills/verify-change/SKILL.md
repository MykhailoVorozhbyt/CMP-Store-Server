---
name: verify-change
description: Use before saying a code change is done, and when the user asks to check, review or verify the current changes. A static checklist over git diff — architecture rules, tests that were weakened, conventions — that ends in pass/fail with file:line evidence. Does not run Gradle; it tells the user which commands to run.
---

# Verify a change

The user runs builds and tests. This skill checks what can be checked by reading, and names the exact commands
the user should run for the rest.

## 1. Scope

Run `git status --short` and `git diff HEAD`. List the changed files and group them: `server/`, `shared/`,
`component/<x>/<layer>`, `feature/<x>`, `core/*`, `di/`, `build-logic/`, tests.

## 2. Tests were not weakened

For every changed file under `src/*Test/` or `server/src/test/`:
- no deleted `@Test` or assertion without a replacement that checks the same behaviour;
- no assertion loosened (`assertEquals` → `assertTrue(x != null)`, exact error → `is ApiResult.Error`);
- no `@Ignore`, no expected value edited to match a new wrong output;
- new production behaviour has a new test: every `ViewAction` branch, every `NetworkError` → `XError` mapping,
  every server route status.

## 3. Architecture

- Feature imports only `component.<x>.usecase` / `.model`, never `domain_api` / `data`.
- No `data` → another component's `data`; no `usecase` → another `usecase`; entity `usecase` does not read another `domain-api`.
- DTOs and `NetworkError` do not leave `data`; mappers and data sources are `internal`.
- One-shot calls are `suspend` + `ApiResult`; a `Flow` is named `observeX()`.
- `:shared` gets no UI, Compose or client code.
- No `android.*` in any `commonMain`; no `Dispatchers.*` outside `AppDispatchers`; `CancellationException` rethrown.
- Server: errors are `ApiException(status, NetworkError.X)`; multi-table writes in one `dbQuery`; `is_admin` not writable.
- Koin: each module registers only what it owns; `:di` only assembles; no use cases in presentation modules.
- New screen: the `AppNavigationModule` placeholder for its key is removed.
- Any `expect` / `actual` or `src/iosMain` file changed: run the `platform-parity` agent and include its verdict.

## 4. Conventions

- No new comments or KDoc that restate the code (`CLAUDE.md` Code Style).
- Every new `build.gradle.kts` holds only `plugins { alias(...) }`.
- Strings in UI come from resources.

## 5. Project-specific red flags

- No module inside a module: every `include` is exactly `:feature:<name>` or `:component:<name>:<layer>`, and no new
  `build.gradle.kts` appears inside another module's directory. NutriSport nests (`:feature:home:cart:checkout`,
  `:feature:admin_panel:manage_product`); here `checkout`, `manage-product`, `product-details` are sibling modules
  (`:feature:checkout`), sharing code only through components and `core:presentation`. The layer plugins fail the
  build on a wrong path, but a module without the plugin slips through.
<!-- TODO(human): 2–5 more checks for mistakes you have actually seen in this project, one line each. -->

## 6. Report

```
VERDICT: PASS | FAIL
Scope: <n> files — <groups>
Findings:
  FAIL <file>:<line> — <rule> — <what is wrong>
  WARN <file>:<line> — <why it might be wrong>
Run (user): ./gradlew <tasks for the touched modules> validateArchitectureDependencies
```

Pick the Gradle tasks from the touched modules: `:server:test`, `:component:<x>:<layer>:jvmTest`,
`:feature:<x>:jvmTest`, `:di:jvmTest` (any DI change), `allTests -x :server:test` for `core/*` or `:shared`.
Never write PASS when a section above was skipped — say which and why.
