package com.feature.authentication.data.data_source

import com.feature.authentication.data.models.testCustomerDto
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import kotlinx.coroutines.test.runTest
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorCustomerDataSourceTest {

    @Test
    fun getCustomer_returns_success_and_calls_customer_by_id_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        val expected = testCustomerDto
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    respondJson(expected)
                }
            )
        )

        val result = dataSource.getCustomer("customer-id")

        assertEquals(ApiResult.Success(expected), result)
        assertEquals("/customer/customer-id", capturedPath)
        assertEquals(HttpMethod.Get, capturedMethod)
    }

    @Test
    fun getCustomer_maps_not_found_body_to_customer_not_found() = runTest {
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.CUSTOMER_NOT_FOUND.name,
                        status = HttpStatusCode.NotFound,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.getCustomer("missing-id")

        assertEquals(ApiResult.Error(NetworkError.CUSTOMER_NOT_FOUND), result)
    }

    @Test
    fun updateCustomer_returns_success_and_calls_customer_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    respond(
                        content = "",
                        status = HttpStatusCode.OK,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.updateCustomer(testCustomerDto)

        assertEquals("/customer", capturedPath)
        assertEquals(HttpMethod.Put, capturedMethod)
        when (result) {
            is ApiResult.Success -> Unit
            is ApiResult.Error -> error("Expected success, got $result")
        }
    }

    @Test
    fun updateCustomer_maps_server_error_body_to_network_error() = runTest {
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.SERVER_ERROR.name,
                        status = HttpStatusCode.InternalServerError,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.updateCustomer(testCustomerDto)

        assertEquals(ApiResult.Error(NetworkError.SERVER_ERROR), result)
    }
}
