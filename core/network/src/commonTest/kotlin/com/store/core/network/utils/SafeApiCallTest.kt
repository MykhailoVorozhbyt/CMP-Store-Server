package com.store.core.network.utils

import com.store.core.domain.ApiResult
import com.store.core.network.bareTestClient
import com.store.core.network.jsonHeaders
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SafeApiCallTest {

    @Test
    fun request_timeout_is_request_timeout() = runTest {
        val result = callFailingWith(HttpRequestTimeoutException("http://test/product", 1_000))

        assertEquals(ApiResult.Error(NetworkError.REQUEST_TIMEOUT), result)
    }

    @Test
    fun connect_timeout_is_request_timeout() = runTest {
        val result = callFailingWith(ConnectTimeoutException("connect timed out"))

        assertEquals(ApiResult.Error(NetworkError.REQUEST_TIMEOUT), result)
    }

    @Test
    fun unreadable_success_body_is_serialization() = runTest {
        val client = bareTestClient(
            MockEngine { respond(content = "<html>captive portal</html>", status = HttpStatusCode.OK, headers = jsonHeaders()) }
        )

        val result = safeApiCall<Map<String, String>> { client.get("product") }

        assertEquals(ApiResult.Error(NetworkError.SERIALIZATION), result)
    }

    @Test
    fun bare_404_is_generic_not_found() = runTest {
        val client = bareTestClient(
            MockEngine { respond(content = "", status = HttpStatusCode.NotFound, headers = jsonHeaders()) }
        )

        val result = safeApiCall<Map<String, String>> { client.get("product/missing") }

        assertEquals(ApiResult.Error(NetworkError.NOT_FOUND), result)
    }

    @Test
    fun error_code_in_body_wins_over_status() = runTest {
        val client = bareTestClient(
            MockEngine {
                respond(
                    content = NetworkError.PRODUCT_NOT_FOUND.name,
                    status = HttpStatusCode.NotFound,
                    headers = jsonHeaders(),
                )
            }
        )

        val result = safeApiCall<Map<String, String>> { client.get("product/missing") }

        assertEquals(ApiResult.Error(NetworkError.PRODUCT_NOT_FOUND), result)
    }

    @Test
    fun cancellation_is_rethrown() = runTest {
        assertFailsWith<CancellationException> {
            callFailingWith(CancellationException("scope cancelled"))
        }
    }

    private suspend fun callFailingWith(throwable: Throwable): ApiResult<Map<String, String>, NetworkError> {
        val client = bareTestClient(MockEngine { throw throwable })
        return safeApiCall { client.get("product") }
    }
}
