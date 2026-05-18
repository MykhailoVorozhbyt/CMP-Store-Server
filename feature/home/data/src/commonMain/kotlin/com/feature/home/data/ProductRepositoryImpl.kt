package com.feature.home.data

import com.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.ApiResult
import org.cmp.store.network.NetworkError

class ProductRepositoryImpl(
    private val api: RemoteDataSource,
) : ProductRepository {

    override fun readDiscountedProducts(): Flow<Result<List<Product>>> =
        emitProducts(api::getDiscountedProducts)

    override fun readNewProducts(): Flow<Result<List<Product>>> =
        emitProducts(api::getNewProducts)

    override fun readProductByIdFlow(id: String): Flow<Result<Product>> =
        flow {
            when (val result = api.getProduct(id)) {
                is ApiResult.Success -> emit(Result.success(result.data))
                is ApiResult.Error -> emit(Result.failure(Exception(result.error.message)))
            }
        }

    override fun readProductsByIdsFlow(ids: List<String>): Flow<Result<List<Product>>> =
        emitProducts { api.getProductsByIds(ids) }

    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<Result<List<Product>>> =
        emitProducts { api.getProductsByCategory(category) }

    private fun emitProducts(
        apiCall: suspend () -> ApiResult<List<Product>, NetworkError>,
    ): Flow<Result<List<Product>>> = flow {
        when (val result = apiCall()) {
            is ApiResult.Success -> emit(Result.success(result.data))
            is ApiResult.Error -> emit(Result.failure(Exception(result.error.message)))
        }
    }
}
