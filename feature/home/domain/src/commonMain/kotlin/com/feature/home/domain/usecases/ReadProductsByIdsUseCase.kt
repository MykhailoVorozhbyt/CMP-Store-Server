package com.feature.home.domain.usecases

import com.feature.home.domain.data_source.ProductDataSource
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError

class ReadProductsByIdsUseCase(
    private val productDataSource: ProductDataSource
) {
    operator fun invoke(ids: List<String>): Flow<ApiResult<List<Product>, NetworkError>> =
        productDataSource.readProductsByIdsFlow(ids)
}
