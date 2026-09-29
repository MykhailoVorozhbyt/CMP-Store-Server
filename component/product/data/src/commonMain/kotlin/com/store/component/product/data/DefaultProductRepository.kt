package com.store.component.product.data

import com.store.component.product.data.data_source.ProductDataSource
import com.store.component.product.data.mapper.toProductError
import com.store.component.product.domain_api.ProductRepository
import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import com.store.core.domain.mapError
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

internal class DefaultProductRepository(
    private val api: ProductDataSource,
) : ProductRepository {

    override suspend fun readDiscountedProducts(): ApiResult<List<Product>, ProductError> =
        api.readDiscountedProducts().mapError { it.toProductError() }

    override suspend fun readNewProducts(): ApiResult<List<Product>, ProductError> =
        api.readNewProducts().mapError { it.toProductError() }

    override suspend fun readProductById(id: String): ApiResult<Product, ProductError> =
        api.readProductById(id).mapError { it.toProductError() }

    override suspend fun readProductsByIds(ids: List<String>): ApiResult<List<Product>, ProductError> =
        api.readProductsByIds(ids).mapError { it.toProductError() }

    override suspend fun readProductsByCategory(category: ProductCategory): ApiResult<List<Product>, ProductError> =
        api.readProductsByCategory(category).mapError { it.toProductError() }
}
