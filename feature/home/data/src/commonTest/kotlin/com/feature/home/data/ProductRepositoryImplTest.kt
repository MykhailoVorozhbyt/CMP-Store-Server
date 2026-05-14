package com.feature.home.data

import com.feature.home.data.fakes.FakeRemoteDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.network.ApiResult
import org.cmp.store.network.NetworkError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRepositoryImplTest {

    private val sampleProducts = listOf(
        Product(
            id = "protein-whey-1",
            title = "Whey Protein Gold",
            description = "Fast-absorbing whey protein.",
            thumbnail = "",
            categoryId = ProductCategory.Protein.id,
            measurementId = 1L,
            currencyId = 840L,
            flavors = listOf("Chocolate"),
            weight = 900,
            price = 54.99,
            isDiscounted = true,
        ),
        Product(
            id = "creatine-1",
            title = "Creatine Monohydrate",
            description = "Classic creatine.",
            thumbnail = "",
            categoryId = ProductCategory.Creatine.id,
            measurementId = 1L,
            currencyId = 840L,
            weight = 300,
            price = 24.99,
            isDiscounted = true,
        ),
        Product(
            id = "protein-isolate-1",
            title = "Isolate Pro",
            description = "High-purity isolate.",
            thumbnail = "",
            categoryId = ProductCategory.Protein.id,
            measurementId = 1L,
            currencyId = 840L,
            flavors = listOf("Strawberry"),
            weight = 750,
            price = 69.99,
            isNew = true,
        ),
        Product(
            id = "preworkout-2",
            title = "Pump Matrix",
            description = "Pre-workout for pump.",
            thumbnail = "",
            categoryId = ProductCategory.PreWorkout.id,
            measurementId = 1L,
            currencyId = 840L,
            weight = 390,
            price = 39.99,
            isDiscounted = true,
            isNew = true,
        ),
    )

    private fun createRepository(api: FakeRemoteDataSource) = ProductRepositoryImpl(api)

    // ─── readDiscountedProducts ───────────────────────────────────────────────

    @Test
    fun readDiscountedProducts_returnsSuccessWhenApiSucceeds() = runBlocking {
        val expected = sampleProducts.filter { it.isDiscounted }
        val api = FakeRemoteDataSource().apply {
            discountedProductsResult = ApiResult.Success(expected)
        }

        val result = createRepository(api).readDiscountedProducts().first()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun readDiscountedProducts_returnsFailureWhenApiFails() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            discountedProductsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readDiscountedProducts().first()

        assertTrue(result.isFailure)
    }

    // ─── readNewProducts ──────────────────────────────────────────────────────

    @Test
    fun readNewProducts_returnsSuccessWhenApiSucceeds() = runBlocking {
        val expected = sampleProducts.filter { it.isNew }
        val api = FakeRemoteDataSource().apply {
            newProductsResult = ApiResult.Success(expected)
        }

        val result = createRepository(api).readNewProducts().first()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun readNewProducts_returnsFailureWhenApiFails() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            newProductsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readNewProducts().first()

        assertTrue(result.isFailure)
    }

    // ─── readProductByIdFlow ──────────────────────────────────────────────────

    @Test
    fun readProductByIdFlow_returnsSuccessWhenApiSucceeds() = runBlocking {
        val expected = sampleProducts.first()
        val api = FakeRemoteDataSource().apply {
            productByIdResult = ApiResult.Success(expected)
        }

        val result = createRepository(api).readProductByIdFlow(expected.id).first()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun readProductByIdFlow_returnsFailureWhenApiFails() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            productByIdResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readProductByIdFlow("protein-whey-1").first()

        assertTrue(result.isFailure)
    }

    // ─── readProductsByIdsFlow ────────────────────────────────────────────────

    @Test
    fun readProductsByIdsFlow_returnsSuccessWhenApiSucceeds() = runBlocking {
        val ids = listOf("protein-whey-1", "creatine-1")
        val expected = sampleProducts.filter { it.id in ids }
        val api = FakeRemoteDataSource().apply {
            productsByIdsResult = ApiResult.Success(expected)
        }

        val result = createRepository(api).readProductsByIdsFlow(ids).first()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun readProductsByIdsFlow_returnsFailureWhenApiFails() = runBlocking {
        val ids = listOf("protein-whey-1", "creatine-1")
        val api = FakeRemoteDataSource().apply {
            productsByIdsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readProductsByIdsFlow(ids).first()

        assertTrue(result.isFailure)
    }

    // ─── readProductsByCategoryFlow ───────────────────────────────────────────

    @Test
    fun readProductsByCategoryFlow_returnsSuccessWhenApiSucceeds() = runBlocking {
        val expected = sampleProducts.filter { it.categoryId == ProductCategory.Protein.id }
        val api = FakeRemoteDataSource().apply {
            productsByCategoryResult = ApiResult.Success(expected)
        }

        val result =
            createRepository(api).readProductsByCategoryFlow(ProductCategory.Protein).first()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun readProductsByCategoryFlow_returnsFailureWhenApiFails() =
        runBlocking {
            val api = FakeRemoteDataSource().apply {
                productsByCategoryResult = ApiResult.Error(NetworkError.NO_INTERNET)
            }

            val result =
                createRepository(api).readProductsByCategoryFlow(ProductCategory.Protein).first()

            assertTrue(result.isFailure)
        }
}
