package com.feature.home.domain.usecases

import com.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.product.Product

class ReadProductByIdUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(id: String): Flow<Result<Product>> =
        repository.readProductByIdFlow(id)
}
