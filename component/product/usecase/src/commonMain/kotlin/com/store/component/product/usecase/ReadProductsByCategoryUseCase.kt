package com.store.component.product.usecase

import com.store.component.product.domain_api.ProductRepository
import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

class ReadProductsByCategoryUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(category: ProductCategory): ApiResult<List<Product>, ProductError> =
        repository.readProductsByCategory(category)
}
