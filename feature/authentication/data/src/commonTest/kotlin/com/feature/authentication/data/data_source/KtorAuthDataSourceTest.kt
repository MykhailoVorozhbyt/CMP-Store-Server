package com.feature.authentication.data.data_source

import com.feature.authentication.data.model.AuthRequestDto
import com.feature.authentication.data.model.AuthResponseDto
import com.feature.authentication.data.models.testCustomerDto
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.auth.AuthProvider
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorAuthDataSourceTest {

    @Test
    fun authorize_returns_success_and_calls_auth_authorize_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        val expected = AuthResponseDto(
            accessToken = "token",
            refreshToken = "refresh-token",
            customer = testCustomerDto,
            isNewAccount = true,
            provider = AuthProvider.MANUAL
        )
        val dataSource = KtorAuthDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    respondJson(expected)
                }
            )
        )

        val result = dataSource.authorize(
            AuthRequestDto(
                provider = AuthProvider.MANUAL,
                email = "user@example.com",
                password = "secret123"
            )
        )

        assertEquals(ApiResult.Success(expected), result)
        assertEquals("/auth/authorize", capturedPath)
        assertEquals(HttpMethod.Post, capturedMethod)
    }

    @Test
    fun logout_calls_auth_logout_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        val dataSource = KtorAuthDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    respond(content = "", status = HttpStatusCode.OK)
                }
            )
        )

        dataSource.logout()

        assertEquals("/auth/logout", capturedPath)
        assertEquals(HttpMethod.Post, capturedMethod)
    }

    @Test
    fun authorize_maps_invalid_credentials_body_to_network_error() = runTest {
        val dataSource = KtorAuthDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.INVALID_CREDENTIALS.name,
                        status = HttpStatusCode.Unauthorized,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.authorize(
            AuthRequestDto(
                provider = AuthProvider.MANUAL,
                email = "user@example.com",
                password = "wrong"
            )
        )

        assertEquals(ApiResult.Error(NetworkError.INVALID_CREDENTIALS), result)
    }

    @Test
    fun authorize_maps_server_status_to_server_error_when_body_is_not_recognized() = runTest {
        val dataSource = KtorAuthDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = "Internal error",
                        status = HttpStatusCode.InternalServerError,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.authorize(
            AuthRequestDto(
                provider = AuthProvider.MANUAL,
                email = "user@example.com",
                password = "secret123"
            )
        )

        assertEquals(ApiResult.Error(NetworkError.SERVER_ERROR), result)
    }

    @Test
    fun authorize_returns_unknown_when_response_body_cannot_be_deserialized() = runTest {
        val dataSource = KtorAuthDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = """{"broken":true}""",
                        status = HttpStatusCode.OK,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.authorize(
            AuthRequestDto(
                provider = AuthProvider.MANUAL,
                email = "user@example.com",
                password = "secret123"
            )
        )

        assertEquals(ApiResult.Error(NetworkError.UNKNOWN), result)
    }
}
