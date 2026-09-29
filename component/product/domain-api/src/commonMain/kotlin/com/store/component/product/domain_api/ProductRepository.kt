package com.store.component.product.domain_api

import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

interface ProductRepository {
    suspend fun readDiscountedProducts(): ApiResult<List<Product>, ProductError>
    suspend fun readNewProducts(): ApiResult<List<Product>, ProductError>
    suspend fun readProductById(id: String): ApiResult<Product, ProductError>
    suspend fun readProductsByIds(ids: List<String>): ApiResult<List<Product>, ProductError>
    suspend fun readProductsByCategory(category: ProductCategory): ApiResult<List<Product>, ProductError>
}
