package com.feature.home.domain.usecases

import com.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product

class ReadDiscountedProductsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Product>>> =
        repository.readDiscountedProducts()
}
