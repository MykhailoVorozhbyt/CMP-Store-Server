package com.store.core.network.api

import com.store.core.domain.ApiResult
import com.store.core.network.bareTestClient
import com.store.core.network.respondJson
import com.store.core.network.tokensJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestData
import io.ktor.content.TextContent
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import kotlinx.coroutines.test.runTest
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionRefreshCallTest {

    @Test
    fun refresh_posts_the_token_to_auth_refresh_and_returns_the_new_pair() = runTest {
        var request: HttpRequestData? = null
        val call = SessionRefreshCall(
            bareTestClient(
                MockEngine {
                    request = it
                    respondJson(tokensJson("new-access", "new-refresh"))
                }
            )
        )

        val result = call.refresh("old-refresh")

        assertEquals(HttpMethod.Post, request?.method)
        assertEquals("/auth/refresh", request?.url?.fullPath)
        // The token has to travel in the body: it is the credential, and a URL would leak it
        // into logs and proxies.
        assertTrue((request?.body as TextContent).text.contains("old-refresh"))
        assertTrue(result is ApiResult.Success)
        assertEquals("new-access", result.data.accessToken)
        assertEquals("new-refresh", result.data.refreshToken)
    }

    @Test
    fun refresh_maps_an_unknown_or_expired_token_to_invalid_refresh_token() = runTest {
        val result = refreshFailingWith(
            NetworkError.INVALID_REFRESH_TOKEN.name,
            HttpStatusCode.Unauthorized,
        )

        assertEquals(ApiResult.Error(NetworkError.INVALID_REFRESH_TOKEN), result)
    }

    @Test
    fun refresh_maps_a_replayed_token_to_token_reuse_detected() = runTest {
        // The server answers this when it has just torn down the whole token family, so the
        // client must be able to tell it apart from an ordinary expiry.
        val result = refreshFailingWith(
            NetworkError.TOKEN_REUSE_DETECTED.name,
            HttpStatusCode.Unauthorized,
        )

        assertEquals(ApiResult.Error(NetworkError.TOKEN_REUSE_DETECTED), result)
    }

    @Test
    fun refresh_maps_an_unrecognised_server_failure_to_server_error() = runTest {
        val result = refreshFailingWith("boom", HttpStatusCode.InternalServerError)

        assertEquals(ApiResult.Error(NetworkError.SERVER_ERROR), result)
    }

    private suspend fun refreshFailingWith(body: String, status: HttpStatusCode) =
        SessionRefreshCall(
            bareTestClient(MockEngine { respondJson(body, status) })
        ).refresh("old-refresh")
}