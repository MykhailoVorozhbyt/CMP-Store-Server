package com.feature.home.domain.usecases

import com.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

class ReadProductsByCategoryUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(category: ProductCategory): Flow<Result<List<Product>>> =
        repository.readProductsByCategoryFlow(category)
}
