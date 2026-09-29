package com.store.test.fakes

import com.store.component.product.domain_api.ProductRepository
import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

class FakeProductRepository : ProductRepository {
    var listResult: ApiResult<List<Product>, ProductError> = ApiResult.Success(emptyList())
    var productResult: ApiResult<Product, ProductError> = ApiResult.Error(ProductError.NotFound)
    var lastRequestedId: String? = null
    var lastRequestedIds: List<String>? = null
    var lastRequestedCategory: ProductCategory? = null

    override suspend fun readDiscountedProducts(): ApiResult<List<Product>, ProductError> = listResult

    override suspend fun readNewProducts(): ApiResult<List<Product>, ProductError> = listResult

    override suspend fun readProductById(id: String): ApiResult<Product, ProductError> {
        lastRequestedId = id
        return productResult
    }

    override suspend fun readProductsByIds(ids: List<String>): ApiResult<List<Product>, ProductError> {
        lastRequestedIds = ids
        return listResult
    }

    override suspend fun readProductsByCategory(category: ProductCategory): ApiResult<List<Product>, ProductError> {
        lastRequestedCategory = category
        return listResult
    }
}
