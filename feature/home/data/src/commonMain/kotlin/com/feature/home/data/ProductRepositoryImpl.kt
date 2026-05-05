package com.feature.home.data

import com.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

class ProductRepositoryImpl : ProductRepository {

    override fun readDiscountedProducts(): Flow<Result<List<Product>>> =
        notImplementedProductsFlow()

    override fun readNewProducts(): Flow<Result<List<Product>>> =
        notImplementedProductsFlow()

    override fun readProductByIdFlow(id: String): Flow<Result<Product>> =
        flowOf(Result.failure(productApiNotImplemented()))

    override fun readProductsByIdsFlow(ids: List<String>): Flow<Result<List<Product>>> =
        notImplementedProductsFlow()

    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<Result<List<Product>>> =
        notImplementedProductsFlow()

    private fun notImplementedProductsFlow(): Flow<Result<List<Product>>> =
        flowOf(Result.failure(productApiNotImplemented()))

    private fun productApiNotImplemented(): IllegalStateException =
        IllegalStateException(PRODUCTS_NOT_IMPLEMENTED_MESSAGE)

    private companion object {
        const val PRODUCTS_NOT_IMPLEMENTED_MESSAGE = "Product API is not implemented yet."
    }
}
