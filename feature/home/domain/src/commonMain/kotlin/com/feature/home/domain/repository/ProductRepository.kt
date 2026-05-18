package com.feature.home.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

interface ProductRepository {
    fun readDiscountedProducts(): Flow<Result<List<Product>>>
    fun readNewProducts(): Flow<Result<List<Product>>>
    fun readProductByIdFlow(id: String): Flow<Result<Product>>
    fun readProductsByIdsFlow(ids: List<String>): Flow<Result<List<Product>>>
    fun readProductsByCategoryFlow(category: ProductCategory): Flow<Result<List<Product>>>
}
