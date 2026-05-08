package com.feature.home.data

import com.feature.home.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory

class ProductRepositoryImpl : ProductRepository {

    override fun readDiscountedProducts(): Flow<Result<List<Product>>> =
        emitProducts { products.filter { it.id in discountedProductIds } }

    override fun readNewProducts(): Flow<Result<List<Product>>> =
        emitProducts { products.takeLast(4) }

    override fun readProductByIdFlow(id: String): Flow<Result<Product>> =
        flow {
            delay(NETWORK_DELAY_MS)
            val product = products.firstOrNull { it.id == id }
            emit(
                product?.let { Result.success(it) }
                    ?: Result.failure(IllegalArgumentException("Product with id=$id was not found."))
            )
        }

    override fun readProductsByIdsFlow(ids: List<String>): Flow<Result<List<Product>>> =
        emitProducts { products.filter { it.id in ids } }

    override fun readProductsByCategoryFlow(category: ProductCategory): Flow<Result<List<Product>>> =
        emitProducts { products.filter { it.category == category } }

    private fun emitProducts(selector: () -> List<Product>): Flow<Result<List<Product>>> = flow {
        delay(NETWORK_DELAY_MS)
        emit(Result.success(selector()))
    }

    private companion object {
        const val NETWORK_DELAY_MS = 650L

        val discountedProductIds = setOf("protein-whey-1", "creatine-1", "preworkout-2")

        val products = listOf(
            Product(
                id = "protein-whey-1",
                price = 54.99,
                category = ProductCategory.Protein,
            ),
            Product(
                id = "protein-isolate-1",
                price = 69.99,
                category = ProductCategory.Protein,
            ),
            Product(
                id = "creatine-1",
                price = 24.99,
                category = ProductCategory.Creatine,
            ),
            Product(
                id = "creatine-2",
                price = 29.99,
                category = ProductCategory.Creatine,
            ),
            Product(
                id = "preworkout-1",
                price = 34.99,
                category = ProductCategory.PreWorkout,
            ),
            Product(
                id = "preworkout-2",
                price = 39.99,
                category = ProductCategory.PreWorkout,
            ),
            Product(
                id = "gainer-1",
                price = 44.99,
                category = ProductCategory.Gainers,
            ),
            Product(
                id = "shaker-1",
                price = 9.99,
                category = ProductCategory.Accessories,
            ),
        )
    }
}
