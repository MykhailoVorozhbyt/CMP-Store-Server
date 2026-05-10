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
            category = ProductCategory.Protein.title,
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
            category = ProductCategory.Creatine.title,
            weight = 300,
            price = 24.99,
            isDiscounted = true,
        ),
        Product(
            id = "protein-isolate-1",
            title = "Isolate Pro",
            description = "High-purity isolate.",
            thumbnail = "",
            category = ProductCategory.Protein.title,
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
            category = ProductCategory.PreWorkout.title,
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
    fun readDiscountedProducts_returnsFallbackWhenApiFails() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            discountedProductsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readDiscountedProducts().first()

        assertTrue(result.isSuccess)
        val fallbackIds = setOf("protein-whey-1", "creatine-1", "preworkout-2")
        result.getOrThrow().forEach { assertTrue(it.id in fallbackIds) }
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
    fun readNewProducts_returnsLast4LocalProductsWhenApiFails() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            newProductsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readNewProducts().first()

        assertTrue(result.isSuccess)
        assertEquals(4, result.getOrThrow().size)
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
    fun readProductByIdFlow_returnsLocalProductWhenApiFailsAndIdExists() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            productByIdResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readProductByIdFlow("protein-whey-1").first()

        assertTrue(result.isSuccess)
        assertEquals("protein-whey-1", result.getOrThrow().id)
    }

    @Test
    fun readProductByIdFlow_returnsFailureWhenApiFailsAndIdNotFound() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            productByIdResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readProductByIdFlow("non-existent-id").first()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
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
    fun readProductsByIdsFlow_returnsLocalProductsFilteredByIdsWhenApiFails() = runBlocking {
        val ids = listOf("protein-whey-1", "creatine-1")
        val api = FakeRemoteDataSource().apply {
            productsByIdsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result = createRepository(api).readProductsByIdsFlow(ids).first()

        assertTrue(result.isSuccess)
        result.getOrThrow().forEach { assertTrue(it.id in ids) }
    }

    @Test
    fun readProductsByIdsFlow_returnsEmptyListWhenApiFailsAndNoMatchingLocalIds() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            productsByIdsResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result =
            createRepository(api).readProductsByIdsFlow(listOf("unknown-1", "unknown-2")).first()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    // ─── readProductsByCategoryFlow ───────────────────────────────────────────

    @Test
    fun readProductsByCategoryFlow_returnsSuccessWhenApiSucceeds() = runBlocking {
        val expected = sampleProducts.filter { it.category == ProductCategory.Protein.title }
        val api = FakeRemoteDataSource().apply {
            productsByCategoryResult = ApiResult.Success(expected)
        }

        val result =
            createRepository(api).readProductsByCategoryFlow(ProductCategory.Protein).first()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrThrow())
    }

    @Test
    fun readProductsByCategoryFlow_returnsLocalProductsFilteredByCategoryWhenApiFails() =
        runBlocking {
            val api = FakeRemoteDataSource().apply {
                productsByCategoryResult = ApiResult.Error(NetworkError.NO_INTERNET)
            }

            val result =
                createRepository(api).readProductsByCategoryFlow(ProductCategory.Protein).first()

            assertTrue(result.isSuccess)
            result.getOrThrow().forEach { assertEquals(ProductCategory.Protein.title, it.category) }
        }

    @Test
    fun readProductsByCategoryFlow_returnsEmptyListWhenApiFailsAndNoCategoryMatch() = runBlocking {
        val api = FakeRemoteDataSource().apply {
            productsByCategoryResult = ApiResult.Error(NetworkError.NO_INTERNET)
        }

        val result =
            createRepository(api).readProductsByCategoryFlow(ProductCategory.Unknown).first()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }
}