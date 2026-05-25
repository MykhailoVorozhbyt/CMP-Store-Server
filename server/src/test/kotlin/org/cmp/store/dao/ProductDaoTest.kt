package org.cmp.store.dao

import org.cmp.store.database.dao.ProductDaoImpl
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.utils.testDaoDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductDaoTest {

    private val productDao = ProductDaoImpl()

    @Test
    fun seed_inserts_products() = testDaoDatabase {
        val products = listOf(
            productFixture("product-seed-1", ProductCategory.Protein.id),
            productFixture("product-seed-2", ProductCategory.Creatine.id),
        )

        productDao.seed(products)

        assertEquals(products[0], productDao.readById(products[0].id))
        assertEquals(products[1], productDao.readById(products[1].id))
    }

    @Test
    fun seed_ignores_duplicates() = testDaoDatabase {
        val product = productFixture("product-duplicate", ProductCategory.Protein.id)

        productDao.seed(listOf(product))
        productDao.seed(listOf(product))

        val stored = productDao.readByCategory(ProductCategory.Protein)
        assertEquals(1, stored.count { it.id == product.id })
    }

    @Test
    fun readDiscounted_filters_correctly() = testDaoDatabase {
        productDao.seed(
            listOf(
                productFixture("discounted-1", ProductCategory.Protein.id, isDiscounted = true),
                productFixture("discounted-2", ProductCategory.Creatine.id, isDiscounted = true),
                productFixture("regular-1", ProductCategory.PreWorkout.id),
            )
        )

        val stored = productDao.readDiscounted()

        assertEquals(2, stored.size)
        assertTrue(stored.all { it.isDiscounted })
    }

    @Test
    fun readNew_respects_limit() = testDaoDatabase {
        productDao.seed(
            listOf(
                productFixture("new-1", ProductCategory.Protein.id, isNew = true),
                productFixture("new-2", ProductCategory.Protein.id, isNew = true),
                productFixture("new-3", ProductCategory.Protein.id, isNew = true),
                productFixture("new-4", ProductCategory.Protein.id, isNew = true),
                productFixture("new-5", ProductCategory.Protein.id, isNew = true),
            )
        )

        val stored = productDao.readNew(limit = 4)

        assertEquals(4, stored.size)
        assertTrue(stored.all { it.isNew })
    }

    @Test
    fun readById_works() = testDaoDatabase {
        val product = productFixture("product-by-id", ProductCategory.Accessories.id)
        productDao.seed(listOf(product))

        val stored = productDao.readById(product.id)

        assertEquals(product, stored)
    }

    @Test
    fun readByIds_empty_list_returns_empty() = testDaoDatabase {
        val stored = productDao.readByIds(emptyList())

        assertTrue(stored.isEmpty())
    }

    @Test
    fun readByCategory_filters_correctly() = testDaoDatabase {
        productDao.seed(
            listOf(
                productFixture("protein-1", ProductCategory.Protein.id),
                productFixture("protein-2", ProductCategory.Protein.id),
                productFixture("creatine-1", ProductCategory.Creatine.id),
            )
        )

        val stored = productDao.readByCategory(ProductCategory.Protein)

        assertEquals(2, stored.size)
        assertTrue(stored.all { it.categoryId == ProductCategory.Protein.id })
    }

    @Test
    fun flavors_serialization_and_deserialization_works() = testDaoDatabase {
        val product = productFixture(
            id = "product-flavors",
            categoryId = ProductCategory.Gainers.id,
            flavors = listOf("Chocolate", "Salted Caramel", "Berry")
        )
        productDao.seed(listOf(product))

        val stored = productDao.readById(product.id)

        assertEquals(product.flavors, stored?.flavors)
    }

    private fun productFixture(
        id: String,
        categoryId: Long,
        flavors: List<String>? = listOf("Vanilla"),
        isDiscounted: Boolean = false,
        isNew: Boolean = false,
    ) = Product(
        id = id,
        createdAt = 1_717_000_000_000,
        title = "Title $id",
        description = "Description $id",
        thumbnail = "thumb-$id",
        categoryId = categoryId,
        measurementId = 1L,
        currencyId = 840L,
        flavors = flavors,
        weight = 500,
        price = 19.99,
        isDiscounted = isDiscounted,
        isNew = isNew,
    )
}

