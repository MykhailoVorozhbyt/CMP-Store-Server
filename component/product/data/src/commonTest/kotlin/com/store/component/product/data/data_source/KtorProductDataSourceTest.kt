package com.store.component.product.data.data_source

import com.store.test.network.jsonHeaders
import com.store.test.network.respondJson
import com.store.test.network.testHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.fullPath
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorProductDataSourceTest {

    private val sampleProduct = Product(
        id = "protein-whey-1",
        title = "Whey Protein Gold",
        description = "Fast-absorbing whey protein.",
        thumbnail = "",
        categoryId = ProductCategory.Protein.id,
        measurementId = 1L,
        currencyId = 840L,
        flavors = listOf("Chocolate"),
        weight = 900,
        price = 54.99,
        isDiscounted = true,
    )

    @Test
    fun readDiscountedProducts_returns_success_and_calls_discounted_route() = runTest {
        var capturedPath: String? = null
        var capturedMethod: HttpMethod? = null
        val expected = listOf(sampleProduct)
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    capturedMethod = request.method
                    respondJson(expected)
                }
            )
        )

        val result = dataSource.readDiscountedProducts()

        assertEquals(ApiResult.Success(expected), result)
        assertEquals("/product/discounted", capturedPath)
        assertEquals(HttpMethod.Get, capturedMethod)
    }

    @Test
    fun readDiscountedProducts_maps_error_body_to_network_error() = runTest {
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.NO_INTERNET.name,
                        status = HttpStatusCode.ServiceUnavailable,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.readDiscountedProducts()

        assertEquals(ApiResult.Error(NetworkError.NO_INTERNET), result)
    }

    @Test
    fun readNewProducts_returns_success_and_calls_new_route() = runTest {
        var capturedPath: String? = null
        val expected = listOf(sampleProduct)
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    respondJson(expected)
                }
            )
        )

        val result = dataSource.readNewProducts()

        assertEquals(ApiResult.Success(expected), result)
        assertEquals("/product/new", capturedPath)
    }

    @Test
    fun readProductById_returns_success_and_calls_product_by_id_route() = runTest {
        var capturedPath: String? = null
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    respondJson(sampleProduct)
                }
            )
        )

        val result = dataSource.readProductById(sampleProduct.id)

        assertEquals(ApiResult.Success(sampleProduct), result)
        assertEquals("/product/${sampleProduct.id}", capturedPath)
    }

    @Test
    fun readProductById_maps_not_found_body_to_network_error() = runTest {
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine {
                    respond(
                        content = NetworkError.PRODUCT_NOT_FOUND.name,
                        status = HttpStatusCode.NotFound,
                        headers = jsonHeaders()
                    )
                }
            )
        )

        val result = dataSource.readProductById("missing-id")

        assertEquals(ApiResult.Error(NetworkError.PRODUCT_NOT_FOUND), result)
    }

    @Test
    fun readProductsByIds_returns_success_and_calls_by_ids_route() = runTest {
        var capturedPath: String? = null
        val expected = listOf(sampleProduct)
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    respondJson(expected)
                }
            )
        )

        val result = dataSource.readProductsByIds(listOf(sampleProduct.id))

        assertEquals(ApiResult.Success(expected), result)
        assertEquals("/product/by-ids?ids=${sampleProduct.id}", capturedPath)
    }

    @Test
    fun readProductsByCategory_returns_success_and_calls_by_category_route() = runTest {
        var capturedPath: String? = null
        val expected = listOf(sampleProduct)
        val dataSource = KtorProductDataSource(
            client = testHttpClient(
                MockEngine { request ->
                    capturedPath = request.url.fullPath
                    respondJson(expected)
                }
            )
        )

        val result = dataSource.readProductsByCategory(ProductCategory.Protein)

        assertEquals(ApiResult.Success(expected), result)
        assertEquals("/product/by-category/${ProductCategory.Protein.id}", capturedPath)
    }

    @Test
    fun readProductsByCategory_maps_server_error_body_to_network_error() = runTest {
        val dataSource = KtorProductDataSource(
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

        val result = dataSource.readProductsByCategory(ProductCategory.Protein)

        assertEquals(ApiResult.Error(NetworkError.SERVER_ERROR), result)
    }
}
