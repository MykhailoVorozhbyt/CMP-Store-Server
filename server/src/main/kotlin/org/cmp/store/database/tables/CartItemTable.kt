package org.cmp.store.database.tables

import org.jetbrains.exposed.sql.Table

object CartItemTable : Table("cart_items") {
    val id = varchar("id", 128)
    val customerId = varchar("customer_id", 128).references(CustomerTable.id)
    val productId = varchar("product_id", 128)
    val flavor = varchar("flavor", 128).nullable()
    val quantity = integer("quantity")

    override val primaryKey = PrimaryKey(id)
}
