package com.store.component.customer.data.data_source

import com.store.component.customer.data.dto.CustomerProfileDto
import com.store.component.customer.data.testCustomerDto
import com.store.component.customer.data.testCustomerProfileDto
import com.store.core.domain.ApiResult
import com.store.test.network.jsonHeaders
import com.store.test.network.respondJson
import com.store.test.network.testHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.fullPath
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class KtorCustomerDataSourceTest {

    @Test
    fun getCustomer_returns_success_and_calls_customer_by_id_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    respondJson(testCustomerDto)
                }
            )
        )

        val result = dataSource.getCustomer("customer-id")

        assertEquals(ApiResult.Success(testCustomerDto), result)
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
                        headers = jsonHeaders(),
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
                    respond(content = "", status = HttpStatusCode.OK, headers = jsonHeaders())
                }
            )
        )

        val result = dataSource.updateCustomer(testCustomerDto)

        assertEquals("/customer", capturedPath)
        assertEquals(HttpMethod.Put, capturedMethod)
        assertIs<ApiResult.Success<Unit>>(result)
    }

    @Test
    fun updateCustomer_maps_server_error_body_to_network_error() = runTest {
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.SERVER_ERROR.name,
                        status = HttpStatusCode.InternalServerError,
                        headers = jsonHeaders(),
                    )
                }
            )
        )

        val result = dataSource.updateCustomer(testCustomerDto)

        assertEquals(ApiResult.Error(NetworkError.SERVER_ERROR), result)
    }

    @Test
    fun updateProfile_puts_only_profile_fields_to_customer_profile_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        var capturedBody: String? = null
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    capturedBody = (request.body as TextContent).text
                    respondJson(testCustomerDto)
                }
            )
        )

        val result = dataSource.updateProfile("customer-id", testCustomerProfileDto)

        assertEquals("/customer/customer-id/profile", capturedPath)
        assertEquals(HttpMethod.Put, capturedMethod)
        assertEquals(testCustomerProfileDto, Json.decodeFromString<CustomerProfileDto>(assertNotNull(capturedBody)))
        assertFalse(assertNotNull(capturedBody).contains("email"))
        assertIs<ApiResult.Success<Unit>>(result)
    }

    @Test
    fun updateProfile_maps_not_found_body_to_customer_not_found() = runTest {
        val dataSource = KtorCustomerDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.CUSTOMER_NOT_FOUND.name,
                        status = HttpStatusCode.NotFound,
                        headers = jsonHeaders(),
                    )
                }
            )
        )

        val result = dataSource.updateProfile("missing-id", testCustomerProfileDto)

        assertEquals(ApiResult.Error(NetworkError.CUSTOMER_NOT_FOUND), result)
    }
}
