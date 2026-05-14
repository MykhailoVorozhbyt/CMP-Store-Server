package org.cmp.store.database.tables

import org.jetbrains.exposed.v1.core.Table

object ProductTable : Table("products") {
    val id = varchar("id", 128)
    val createdAt = long("created_at")
    val title = varchar("title", 255)
    val description = text("description")
    val thumbnail = varchar("thumbnail", 512)
    val price = double("price")
    val categoryId = long("category_id")
    val measurementId = long("measurement_id")
    val currencyId = long("currency_id")
    val flavors = text("flavors").nullable()
    val weight = integer("weight").nullable()
    val isPopular = bool("is_popular").default(false)
    val isDiscounted = bool("is_discounted").default(false)
    val isNew = bool("is_new").default(false)

    override val primaryKey = PrimaryKey(id)
}
