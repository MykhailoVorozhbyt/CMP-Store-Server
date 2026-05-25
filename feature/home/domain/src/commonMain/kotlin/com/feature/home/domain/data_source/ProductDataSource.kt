package com.feature.home.domain.data_source

import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError

interface ProductDataSource {
    fun readDiscountedProducts(): Flow<ApiResult<List<Product>, NetworkError>>
    fun readNewProducts(): Flow<ApiResult<List<Product>, NetworkError>>
    fun readProductByIdFlow(id: String): Flow<ApiResult<Product, NetworkError>>
    fun readProductsByIdsFlow(ids: List<String>): Flow<ApiResult<List<Product>, NetworkError>>
    fun readProductsByCategoryFlow(category: ProductCategory): Flow<ApiResult<List<Product>, NetworkError>>
}
