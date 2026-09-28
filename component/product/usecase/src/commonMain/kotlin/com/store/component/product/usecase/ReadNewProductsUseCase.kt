package com.store.component.product.usecase

import com.store.component.product.domain_api.ProductRepository
import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import org.cmp.store.domain.product.Product

class ReadNewProductsUseCase(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(): ApiResult<List<Product>, ProductError> = repository.readNewProducts()
}
