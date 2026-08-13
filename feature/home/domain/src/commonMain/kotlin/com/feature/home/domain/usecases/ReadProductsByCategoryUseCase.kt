package com.feature.home.domain.usecases

import com.feature.home.domain.data_source.ProductDataSource
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError

class ReadProductsByCategoryUseCase(
    private val productDataSource: ProductDataSource
) {
    operator fun invoke(category: ProductCategory): Flow<ApiResult<List<Product>, NetworkError>> =
        productDataSource.readProductsByCategoryFlow(category)
}
