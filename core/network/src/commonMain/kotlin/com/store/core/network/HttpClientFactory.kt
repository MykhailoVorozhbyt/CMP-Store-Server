package com.store.core.network

import com.store.core.domain.ApiResult
import com.store.core.network.api.SessionRefreshCall
import com.store.core.network.utils.isSessionTerminal
import com.store.core.security.LocalAuthSessionDataSource
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.cmp.store.SERVER_PORT

fun createHttpClient(localAuthSessionDataSource: LocalAuthSessionDataSource): HttpClient =
    HttpClient { configureStore(localAuthSessionDataSource) }

internal fun createHttpClient(
    localAuthSessionDataSource: LocalAuthSessionDataSource,
    engine: HttpClientEngine,
): HttpClient = HttpClient(engine) { configureStore(localAuthSessionDataSource) }

private fun HttpClientConfig<*>.configureStore(localAuthSessionDataSource: LocalAuthSessionDataSource) {
    expectSuccess = true
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Auth) {
        bearer {
            loadTokens {
                val accessToken = localAuthSessionDataSource.currentAccessToken()
                    ?: return@loadTokens null
                BearerTokens(
                    accessToken = accessToken,
                    refreshToken = localAuthSessionDataSource.currentRefreshToken().orEmpty()
                )
            }

            refreshTokens {
                val refreshToken = localAuthSessionDataSource.currentRefreshToken()
                    ?: return@refreshTokens null

                when (val result = SessionRefreshCall(client).refresh(refreshToken)) {
                    is ApiResult.Error -> {
                        // Only a refusal the server will keep repeating may drop the session.
                        // A transient one fails this call and leaves the tokens for the next.
                        if (result.error.isSessionTerminal) localAuthSessionDataSource.signOut()
                        null
                    }

                    is ApiResult.Success -> {
                        localAuthSessionDataSource.updateTokens(
                            accessToken = result.data.accessToken,
                            refreshToken = result.data.refreshToken
                        )
                        BearerTokens(
                            accessToken = result.data.accessToken,
                            refreshToken = result.data.refreshToken
                        )
                    }
                }
            }
        }
    }
    install(DefaultRequest) {
        port = SERVER_PORT
        url(SERVER_BASE_URL)
        contentType(ContentType.Application.Json)
    }
}