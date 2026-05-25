package com.feature.authentication.data

import com.feature.authentication.data.model.AuthCallback
import com.feature.authentication.data.model.LocalCallbackServer
import com.feature.authentication.data.oauth.GoogleOAuthConfig
import com.feature.authentication.data.oauth.GoogleOAuthConfig.AUTH_TIMEOUT_SECONDS
import com.feature.authentication.data.oauth.GoogleOAuthConfig.GOOGLE_AUTH_URL
import com.feature.authentication.data.oauth.GoogleOAuthConfig.GOOGLE_TOKEN_URL
import com.feature.authentication.data.oauth.JwtParser
import com.feature.authentication.data.oauth.LocalOAuthCallbackServer
import com.feature.authentication.domain.model.DESKTOP_CLIENT_ID
import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.feature.authentication.domain.repository.GoogleSignInService
import com.store.core.utils.extension.runCatchingCancellable
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.IOException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.store.core.domain.ApiResult
import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.seconds

internal fun openSystemBrowser(url: String) {
    if (!Desktop.isDesktopSupported()) {
        throw GoogleSignInError.DesktopNotSupported()
    }
    Desktop.getDesktop().browse(URI(url))
}

class JvmGoogleSignInService(
    private val httpClient: HttpClient,
    private val clientSecret: String,
    private val localOAuth: LocalOAuthCallbackServer = LocalOAuthCallbackServer(),
    private val browserLauncher: (String) -> Unit = ::openSystemBrowser,
) : GoogleSignInService {

    override suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError> = runCatchingCancellable {
        val state = generateRandomString()
        val nonce = generateRandomString()
        val codeVerifier = generateRandomString(byteCount = 64)
        val callbackServer = localOAuth.createCallback(expectedState = state)

        try {
            startServerOrThrow(callbackServer)

            val redirectUrl = GoogleOAuthConfig.redirectUrl(callbackServer.port)
            browserLauncher(
                buildAuthorizationUrl(
                    state = state,
                    nonce = nonce,
                    codeChallenge = createCodeChallenge(codeVerifier),
                    redirectUrl = redirectUrl,
                )
            )
            val callback = withTimeoutOrNull(AUTH_TIMEOUT_SECONDS.seconds) {
                callbackServer.awaitCallback()
            } ?: throw GoogleSignInError.Timeout()
            val tokenResponse = exchangeCodeForTokens(callback.code, codeVerifier, redirectUrl)
            val user = tokenResponse.toAuthUser(expectedNonce = nonce)
            user
        } finally {
            callbackServer.stop()
        }
    }.fold(
        onSuccess = { ApiResult.Success(it) },
        onFailure = { e -> ApiResult.Error(e as? GoogleSignInError ?: GoogleSignInError.Unknown(e)) },
    )

    private fun startServerOrThrow(callbackServer: LocalCallbackServer) {
        try {
            callbackServer.start()
        } catch (e: IOException) {
            throw GoogleSignInError.ServerStartFailed(e)
        }
    }

    private fun buildAuthorizationUrl(
        state: String,
        nonce: String,
        codeChallenge: String,
        redirectUrl: String,
    ): String = buildString {
        append(GOOGLE_AUTH_URL)
        append("?client_id=").append(encode(DESKTOP_CLIENT_ID))
        append("&redirect_uri=").append(encode(redirectUrl))
        append("&response_type=").append(encode("code"))
        append("&scope=").append(encode("openid email profile"))
        append("&state=").append(encode(state))
        append("&nonce=").append(encode(nonce))
        append("&code_challenge=").append(encode(codeChallenge))
        append("&code_challenge_method=S256")
    }

    private suspend fun exchangeCodeForTokens(
        code: String,
        codeVerifier: String,
        redirectUrl: String,
    ): TokenEntity {
        val response = httpClient.submitForm(
            url = GOOGLE_TOKEN_URL,
            formParameters = Parameters.build {
                append("client_id", DESKTOP_CLIENT_ID)
                append("code", code)
                append("code_verifier", codeVerifier)
                append("grant_type", "authorization_code")
                append("redirect_uri", redirectUrl)
                append("client_secret", clientSecret)
            }
        )

        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw GoogleSignInError.TokenExchangeFailed(response.status.value, body)
        }

        val format = Json { ignoreUnknownKeys = true }
        val tokenResponse = format.decodeFromString<GoogleTokenResponse>(body)
        val idToken = tokenResponse.idToken ?: throw GoogleSignInError.MissingIdToken()
        return TokenEntity(idToken, tokenResponse.accessToken)
    }

    private fun TokenEntity.toAuthUser(expectedNonce: String): AuthUserRequest {
        val claims = JwtParser.decodeClaims(idToken)
        if (claims.nonce != expectedNonce) {
            throw GoogleSignInError.NonceMismatch()
        }
        return AuthUserRequest(
            uid = claims.sub, displayName = claims.name, email = claims.email
        )
    }

    private fun createCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(codeVerifier.toByteArray(StandardCharsets.UTF_8))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    private fun generateRandomString(byteCount: Int = 32): String {
        val bytes = ByteArray(byteCount)
        SECURE_RANDOM.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun encode(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8)

    internal suspend fun LocalCallbackServer.awaitCallback(): AuthCallback =
        suspendCancellableCoroutine { cont ->
            callbackFuture.whenComplete { result, error ->
                if (error != null) {
                    cont.resumeWithException(error)
                } else {
                    cont.resume(result)
                }
            }
            cont.invokeOnCancellation {
                callbackFuture.cancel(true)
            }
        }

    companion object {
        private val SECURE_RANDOM = SecureRandom()
    }
}

@Serializable
private data class GoogleTokenResponse(
    @SerialName("id_token")
    val idToken: String? = null,
    @SerialName("access_token")
    val accessToken: String? = null,
)

private data class TokenEntity(
    val idToken: String,
    val accessToken: String?
)