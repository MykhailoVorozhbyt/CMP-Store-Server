package com.store.component.product.data

import com.store.component.product.model.ProductError
import com.store.core.domain.ApiResult
import com.store.core.domain.DataError
import kotlinx.coroutines.test.runTest
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultProductRepositoryTest {
    private val fakeApi = FakeProductDataSource()
    private val repository = DefaultProductRepository(fakeApi)

    private val product = Product(
        id = "protein-whey-1",
        title = "Whey Protein Gold",
        description = "Fast-absorbing whey protein.",
        thumbnail = "",
        categoryId = ProductCategory.Protein.id,
        measurementId = 1L,
        currencyId = 840L,
        price = 54.99,
    )

    @Test
    fun readDiscountedProducts_passesSuccessThrough() = runTest {
        fakeApi.listResult = ApiResult.Success(listOf(product))

        assertEquals(ApiResult.Success(listOf(product)), repository.readDiscountedProducts())
    }

    @Test
    fun readProductById_mapsProductNotFoundToNotFound() = runTest {
        fakeApi.productResult = ApiResult.Error(NetworkError.PRODUCT_NOT_FOUND)

        assertEquals(ApiResult.Error(ProductError.NotFound), repository.readProductById("missing"))
    }

    @Test
    fun readProductsByCategory_mapsInvalidCategory() = runTest {
        fakeApi.listResult = ApiResult.Error(NetworkError.INVALID_PRODUCT_CATEGORY)

        val result = repository.readProductsByCategory(ProductCategory.Protein)

        assertEquals(ApiResult.Error(ProductError.InvalidCategory), result)
        assertEquals(ProductCategory.Protein, fakeApi.lastRequestedCategory)
    }

    @Test
    fun readProductsByIds_mapsTransportFailureToCommonError() = runTest {
        fakeApi.listResult = ApiResult.Error(NetworkError.NO_INTERNET)

        val result = repository.readProductsByIds(listOf("a", "b"))

        assertEquals(ApiResult.Error(ProductError.Common(DataError.NoInternet)), result)
        assertEquals(listOf("a", "b"), fakeApi.lastRequestedIds)
    }
}
