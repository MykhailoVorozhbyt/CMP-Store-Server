package com.feature.home.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.ApiResult
import org.cmp.store.network.NetworkError
import org.cmp.store.network.safeApiCall

class RemoteDataSource(
    private val client: HttpClient,
) {
    suspend fun getDiscountedProducts(): ApiResult<List<Product>, NetworkError> =
        safeApiCall { client.get("$PRODUCT/discounted").body() }

    suspend fun getNewProducts(): ApiResult<List<Product>, NetworkError> =
        safeApiCall { client.get("$PRODUCT/new").body() }

    suspend fun getProduct(id: String): ApiResult<Product, NetworkError> =
        safeApiCall { client.get("$PRODUCT/$id").body() }

    suspend fun getProductsByIds(ids: List<String>): ApiResult<List<Product>, NetworkError> =
        safeApiCall {
            client.get("$PRODUCT/by-ids") {
                parameter("ids", ids.joinToString(","))
            }.body()
        }

    suspend fun getProductsByCategory(category: ProductCategory): ApiResult<List<Product>, NetworkError> =
        safeApiCall { client.get("$PRODUCT/by-category/${category.name}").body() }

    private companion object {
        const val PRODUCT = "product"
    }
}
