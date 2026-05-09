package org.cmp.store.database.dao

import org.cmp.store.database.tables.ProductTable
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object ProductDao {

    suspend fun seed(products: List<Product>) = newSuspendedTransaction {
        products.forEach { product ->
            ProductTable.insertIgnore {
                it[id] = product.id
                it[createdAt] = product.createdAt
                it[title] = product.title
                it[description] = product.description
                it[thumbnail] = product.thumbnail
                it[price] = product.price
                it[category] = product.category
                it[flavors] = product.flavors?.joinToString(FLAVORS_SEPARATOR)
                it[weight] = product.weight
                it[isPopular] = product.isPopular
                it[isDiscounted] = product.isDiscounted
                it[isNew] = product.isNew
            }
        }
    }

    suspend fun readDiscounted(ids: Set<String>): List<Product> = newSuspendedTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.isDiscounted eq true }
            .map(::toProduct)
    }

    suspend fun readNew(limit: Int): List<Product> = newSuspendedTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.isNew eq true }
            .limit(limit)
            .map(::toProduct)
    }

    suspend fun readById(id: String): Product? = newSuspendedTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.id eq id }
            .singleOrNull()
            ?.let(::toProduct)
    }

    suspend fun readByIds(ids: List<String>): List<Product> = newSuspendedTransaction {
        if (ids.isEmpty()) return@newSuspendedTransaction emptyList()
        ProductTable
            .selectAll()
            .where { ProductTable.id inList ids }
            .map(::toProduct)
    }

    suspend fun readByCategory(category: ProductCategory): List<Product> = newSuspendedTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.category eq category.title }
            .map(::toProduct)
    }

    private fun toProduct(row: org.jetbrains.exposed.sql.ResultRow): Product =
        Product(
            id = row[ProductTable.id],
            createdAt = row[ProductTable.createdAt],
            title = row[ProductTable.title],
            description = row[ProductTable.description],
            thumbnail = row[ProductTable.thumbnail],
            category = row[ProductTable.category],
            flavors = row[ProductTable.flavors]
                ?.split(FLAVORS_SEPARATOR)
                ?.filter(String::isNotBlank),
            weight = row[ProductTable.weight],
            price = row[ProductTable.price],
            isPopular = row[ProductTable.isPopular],
            isDiscounted = row[ProductTable.isDiscounted],
            isNew = row[ProductTable.isNew],
        )
}

private const val FLAVORS_SEPARATOR = "|"
