package com.store.component.product.usecase

import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import com.store.test.fakes.FakeProductRepository
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import kotlin.test.Test
import kotlin.test.assertEquals

class ProductUseCasesTest {

    private val repository = FakeProductRepository()

    private val product = Product(
        id = "product-1",
        title = "Whey Protein Gold",
        description = "Fast-absorbing whey protein.",
        thumbnail = "",
        categoryId = ProductCategory.Protein.id,
        measurementId = 1L,
        currencyId = 840L,
        price = 54.99,
    )

    @Test
    fun readDiscountedProductsUseCase_returns_repository_result() = runTest {
        repository.listResult = ApiResult.Success(listOf(product))

        assertEquals(ApiResult.Success(listOf(product)), ReadDiscountedProductsUseCase(repository)())
    }

    @Test
    fun readNewProductsUseCase_returns_repository_result() = runTest {
        repository.listResult = ApiResult.Success(listOf(product))

        assertEquals(ApiResult.Success(listOf(product)), ReadNewProductsUseCase(repository)())
    }

    @Test
    fun readProductByIdUseCase_passes_the_id() = runTest {
        repository.productResult = ApiResult.Success(product)

        val result = ReadProductByIdUseCase(repository)("product-1")

        assertEquals(ApiResult.Success(product), result)
        assertEquals("product-1", repository.lastRequestedId)
    }

    @Test
    fun readProductsByIdsUseCase_passes_the_ids() = runTest {
        repository.listResult = ApiResult.Error(ProductError.NotFound)

        val result = ReadProductsByIdsUseCase(repository)(listOf("a", "b"))

        assertEquals(ApiResult.Error(ProductError.NotFound), result)
        assertEquals(listOf("a", "b"), repository.lastRequestedIds)
    }

    @Test
    fun readProductsByCategoryUseCase_passes_the_category() = runTest {
        ReadProductsByCategoryUseCase(repository)(ProductCategory.Creatine)

        assertEquals(ProductCategory.Creatine, repository.lastRequestedCategory)
    }
}
