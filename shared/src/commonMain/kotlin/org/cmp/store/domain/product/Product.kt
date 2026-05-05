package org.cmp.store.domain.product

data class Product(
    val id: String,
    val price: Double,
    val category: ProductCategory = ProductCategory.Unknown,
)
