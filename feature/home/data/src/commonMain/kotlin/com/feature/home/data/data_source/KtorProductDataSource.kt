package com.feature.home.data.data_source

import com.feature.home.domain.data_source.ProductDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import com.store.core.domain.ApiResult
import com.store.core.network.utils.safeApiCall
import org.cmp.store.network.NetworkError

class KtorProductDataSource(
    private val client: HttpClient,
) : ProductDataSource {

    override fun readDiscountedProducts(): Flow<ApiResult<List<Product>, NetworkError>> = flow {
        emit(safeApiCall<List<Product>> { client.get("$PRODUCT/discounted") })
    }

    override fun readNewProducts(): Flow<ApiResult<List<Product>, NetworkError>> = flow {
        emit(safeApiCall<List<Product>> { client.get("$PRODUCT/new") })
    }

    override fun readProductByIdFlow(id: String): Flow<ApiResult<Product, NetworkError>> = flow {
        emit(safeApiCall<Product> { client.get("$PRODUCT/$id") })
    }

    override fun readProductsByIdsFlow(ids: List<String>): Flow<ApiResult<List<Product>, NetworkError>> = flow {
        emit(
            safeApiCall<List<Product>> {
                client.get("$PRODUCT/by-ids") {
                    ids.forEach { parameter("ids", it) }
                }
            }
        )
    }

    override fun readProductsByCategoryFlow(
        category: ProductCategory,
    ): Flow<ApiResult<List<Product>, NetworkError>> = flow {
        emit(safeApiCall<List<Product>> { client.get("$PRODUCT/by-category/${category.id}") })
    }

    private companion object {
        const val PRODUCT = "product"
    }
}
