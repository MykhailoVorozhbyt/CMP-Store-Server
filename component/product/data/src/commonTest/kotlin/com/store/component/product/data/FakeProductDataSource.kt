package com.store.component.product.data

import com.store.component.product.data.data_source.ProductDataSource
import com.store.core.domain.ApiResult
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.NetworkError

internal class FakeProductDataSource : ProductDataSource {
    var listResult: ApiResult<List<Product>, NetworkError> = ApiResult.Success(emptyList())
    var productResult: ApiResult<Product, NetworkError> = ApiResult.Error(NetworkError.UNKNOWN)
    var lastRequestedIds: List<String>? = null
    var lastRequestedCategory: ProductCategory? = null

    override suspend fun readDiscountedProducts(): ApiResult<List<Product>, NetworkError> = listResult

    override suspend fun readNewProducts(): ApiResult<List<Product>, NetworkError> = listResult

    override suspend fun readProductById(id: String): ApiResult<Product, NetworkError> = productResult

    override suspend fun readProductsByIds(ids: List<String>): ApiResult<List<Product>, NetworkError> {
        lastRequestedIds = ids
        return listResult
    }

    override suspend fun readProductsByCategory(category: ProductCategory): ApiResult<List<Product>, NetworkError> {
        lastRequestedCategory = category
        return listResult
    }
}
