package org.cmp.store.database.dao

import org.cmp.store.database.tables.ProductTable
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction

object ProductDao {

    suspend fun seed(products: List<Product>) = suspendTransaction {
        products.forEach { product ->
            ProductTable.insertIgnore {
                it[id] = product.id
                it[createdAt] = product.createdAt
                it[title] = product.title
                it[description] = product.description
                it[thumbnail] = product.thumbnail
                it[price] = product.price
                it[categoryId] = product.categoryId
                it[measurementId] = product.measurementId
                it[currencyId] = product.currencyId
                it[flavors] = product.flavors?.joinToString(FLAVORS_SEPARATOR)
                it[weight] = product.weight
                it[isPopular] = product.isPopular
                it[isDiscounted] = product.isDiscounted
                it[isNew] = product.isNew
            }
        }
    }

    suspend fun readDiscounted(): List<Product> = suspendTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.isDiscounted eq true }
            .map(::toProduct)
    }

    suspend fun readNew(limit: Int): List<Product> = suspendTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.isNew eq true }
            .limit(limit)
            .map(::toProduct)
    }

    suspend fun readById(id: String): Product? = suspendTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.id eq id }
            .singleOrNull()
            ?.let(::toProduct)
    }

    suspend fun readByIds(ids: List<String>): List<Product> = suspendTransaction {
        if (ids.isEmpty()) return@suspendTransaction emptyList()
        ProductTable
            .selectAll()
            .where { ProductTable.id inList ids }
            .map(::toProduct)
    }

    suspend fun readByCategory(category: ProductCategory): List<Product> = suspendTransaction {
        ProductTable
            .selectAll()
            .where { ProductTable.categoryId eq category.id }
            .map(::toProduct)
    }

    private fun toProduct(row: org.jetbrains.exposed.v1.core.ResultRow): Product =
        Product(
            id = row[ProductTable.id],
            createdAt = row[ProductTable.createdAt],
            title = row[ProductTable.title],
            description = row[ProductTable.description],
            thumbnail = row[ProductTable.thumbnail],
            categoryId = row[ProductTable.categoryId],
            measurementId = row[ProductTable.measurementId],
            currencyId = row[ProductTable.currencyId],
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
