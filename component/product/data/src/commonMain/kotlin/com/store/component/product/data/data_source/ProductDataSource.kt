package com.store.component.product.data.data_source

import com.store.core.domain.ApiResult
import com.store.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.NetworkError

internal interface ProductDataSource {
    suspend fun readDiscountedProducts(): ApiResult<List<Product>, NetworkError>
    suspend fun readNewProducts(): ApiResult<List<Product>, NetworkError>
    suspend fun readProductById(id: String): ApiResult<Product, NetworkError>
    suspend fun readProductsByIds(ids: List<String>): ApiResult<List<Product>, NetworkError>
    suspend fun readProductsByCategory(category: ProductCategory): ApiResult<List<Product>, NetworkError>
}

internal class KtorProductDataSource(
    private val client: HttpClient,
) : ProductDataSource {

    override suspend fun readDiscountedProducts(): ApiResult<List<Product>, NetworkError> =
        safeApiCall { client.get("$PRODUCT/discounted") }

    override suspend fun readNewProducts(): ApiResult<List<Product>, NetworkError> =
        safeApiCall { client.get("$PRODUCT/new") }

    override suspend fun readProductById(id: String): ApiResult<Product, NetworkError> =
        safeApiCall { client.get("$PRODUCT/$id") }

    override suspend fun readProductsByIds(ids: List<String>): ApiResult<List<Product>, NetworkError> =
        safeApiCall {
            client.get("$PRODUCT/by-ids") {
                ids.forEach { parameter("ids", it) }
            }
        }

    override suspend fun readProductsByCategory(category: ProductCategory): ApiResult<List<Product>, NetworkError> =
        safeApiCall { client.get("$PRODUCT/by-category/${category.id}") }

    private companion object {
        const val PRODUCT = "product"
    }
}
