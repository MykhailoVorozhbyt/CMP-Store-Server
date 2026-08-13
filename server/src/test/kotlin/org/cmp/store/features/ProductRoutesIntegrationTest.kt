package org.cmp.store.features

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import org.cmp.store.database.dao.ProductDaoImpl
import org.cmp.store.utils.decodeJson
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.utils.testServerApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRoutesIntegrationTest {

    private val productDao = ProductDaoImpl()

    @Test
    fun get_discounted_products_returns_only_discounted_products() = testServerApplication {
        seedProducts(
            productFixture(
                "discounted-1",
                categoryId = ProductCategory.Protein.id,
                isDiscounted = true
            ),
            productFixture(
                "discounted-2",
                categoryId = ProductCategory.Creatine.id,
                isDiscounted = true
            ),
            productFixture("regular-1", categoryId = ProductCategory.PreWorkout.id),
        )

        val response = client.get("/product/discounted")

        val body = response.decodeJson<List<Product>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, body.size)
        assertTrue(body.all { it.isDiscounted })
    }

    @Test
    fun get_new_products_returns_max_4_new_products() = testServerApplication {
        seedProducts(
            productFixture("new-1", categoryId = ProductCategory.Protein.id, isNew = true),
            productFixture("new-2", categoryId = ProductCategory.Protein.id, isNew = true),
            productFixture("new-3", categoryId = ProductCategory.Protein.id, isNew = true),
            productFixture("new-4", categoryId = ProductCategory.Protein.id, isNew = true),
            productFixture("new-5", categoryId = ProductCategory.Protein.id, isNew = true),
            productFixture("old-1", categoryId = ProductCategory.Creatine.id),
        )

        val response = client.get("/product/new")

        val body = response.decodeJson<List<Product>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(4, body.size)
        assertTrue(body.all { it.isNew })
    }

    @Test
    fun get_product_by_ids_returns_matching_products() = testServerApplication {
        val first = productFixture("product-a", categoryId = ProductCategory.Protein.id)
        val second = productFixture("product-b", categoryId = ProductCategory.Creatine.id)
        seedProducts(
            first,
            second,
            productFixture("product-c", categoryId = ProductCategory.PreWorkout.id),
        )

        val response = client.get("/product/by-ids?ids=${first.id}&ids=${second.id}")

        val body = response.decodeJson<List<Product>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(setOf(first.id, second.id), body.map { it.id }.toSet())
    }

    @Test
    fun get_product_by_ids_with_empty_ids_returns_empty_list() = testServerApplication {
        seedProducts(productFixture("product-a", categoryId = ProductCategory.Protein.id))

        val response = client.get("/product/by-ids")

        val body = response.decodeJson<List<Product>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(body.isEmpty())
    }

    @Test
    fun get_product_by_category_returns_category_products() = testServerApplication {
        seedProducts(
            productFixture("protein-1", categoryId = ProductCategory.Protein.id),
            productFixture("protein-2", categoryId = ProductCategory.Protein.id),
            productFixture("creatine-1", categoryId = ProductCategory.Creatine.id),
        )

        val response = client.get("/product/by-category/${ProductCategory.Protein.id}")

        val body = response.decodeJson<List<Product>>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(2, body.size)
        assertTrue(body.all { it.categoryId == ProductCategory.Protein.id })
    }

    @Test
    fun invalid_category_id_returns_400() = testServerApplication {
        val response = client.get("/product/by-category/999")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Unknown category: 999", response.bodyAsText())
    }

    @Test
    fun get_product_by_id_returns_product() = testServerApplication {
        val product = productFixture("product-get", categoryId = ProductCategory.Accessories.id)
        seedProducts(product)

        val response = client.get("/product/${product.id}")

        val body = response.decodeJson<Product>()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(product, body)
    }

    @Test
    fun unknown_product_id_returns_404() = testServerApplication {
        val response = client.get("/product/missing-product")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Product not found", response.bodyAsText())
    }

    private suspend fun seedProducts(vararg products: Product) {
        productDao.seed(products.toList())
    }

    private fun productFixture(
        id: String,
        categoryId: Long,
        isDiscounted: Boolean = false,
        isNew: Boolean = false,
    ): Product = Product(
        id = id,
        createdAt = 1_717_000_000_000,
        title = "Title $id",
        description = "Description $id",
        thumbnail = "https://example.com/$id.png",
        categoryId = categoryId,
        measurementId = 1L,
        currencyId = 840L,
        flavors = listOf("Chocolate", "Vanilla"),
        weight = 900,
        price = 49.99,
        isDiscounted = isDiscounted,
        isNew = isNew,
    )
}
