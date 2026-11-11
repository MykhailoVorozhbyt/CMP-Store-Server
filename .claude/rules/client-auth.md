---
paths:
  - "component/auth/**"
  - "core/network/**"
  - "core/security/**"
---

## Client Auth & Networking

- **Manual sign-in:** `AuthenticationViewAction.OnSignInClick` → `SignInUseCase(email, password)` → `AuthRepository.authorize` → POST `auth/authorize`. There is no separate registration screen.
- **Google, mobile:** KMP-Auth `GoogleButtonUiContainerFirebase` (via the `expect fun PlatformGoogleButton`) → `FirebaseUser` → `SignInUseCase(AuthUserRequest)` → `authorize(provider = GOOGLE)`.
- **Google, desktop:** `JvmGoogleSignInService` — full OAuth 2.0 Authorization Code + PKCE (S256) with `state`/`nonce`, system browser, `LocalOAuthCallbackServer` on localhost, 180 s timeout. The client secret is resolved at runtime by `DesktopClientSecretResolver` (env `DESKTOP_CLIENT_SECRET` → repo-root `secrets.properties`) and is **never compiled in**.
- **Token storage:** custom `SecureStorage` per platform (AndroidKeyStore AES/GCM + SharedPreferences; iOS Keychain; JVM AES/GCM file vault under `~/.store_app`). Not DataStore, not multiplatform-settings. The session contract is `LocalAuthSessionDataSource` (`core:security`,
used by `core:network` for bearer tokens and by `component/customer/data` for the user id); its implementations live in
`component/auth/data`: `DefaultLocalAuthSessionDataSource` (JVM; mutex-guarded, lazily hydrated cache) and
`FirebaseLocalAuthSessionDataSource` (Android/iOS; `signOut` also signs out of Firebase — there is no Firebase uid
fallback, the server-issued session is the only proof of sign-in), picked by expect/actual
`provideAuthSessionDataSource`.
- **Transparent refresh:** Ktor `Auth` plugin `bearer { loadTokens / refreshTokens }` in `core/network/HttpClientFactory.kt`. The refresh call sets `AuthCircuitBreaker` to avoid recursion; on failure it signs out locally and returns null.
- **Errors:** wrap client calls in `safeApiCall { }` → `ApiResult<D, NetworkError>` (`Success` / `Error` / `NoInternet`) with `onSuccess` / `onError` / `onNoInternet` / `mapSuccess` / `mapError` from `:core:domain`.
  - HTTP errors: the `NetworkError` name in the body wins; without it the status decides (bare 404 → generic `NOT_FOUND`, never an entity-specific code).
  - Transport failures: timeouts (`HttpRequestTimeoutException`, `ConnectTimeoutException`, `SocketTimeoutException`, iOS `NSURLErrorTimedOut`) → `REQUEST_TIMEOUT`; unreachable host (JVM/Android `UnknownHostException` / `ConnectException` / …, iOS NSURLError −1009/−1003/−1004/−1005/−1020 via expect/actual `platformNetworkError()`) → `NO_INTERNET`, which `safeApiCall` returns as `ApiResult.NoInternet` (not an `Error`; `DataError` has no NoInternet); unreadable body → `SERIALIZATION`; anything else → `UNKNOWN`. Every transport failure is logged through `Logger`. `CancellationException` is always rethrown.
  - Server routes must throw `ApiException(status, NetworkError.X)` — never `call.respond(status, "free text")`, which the client cannot map.

