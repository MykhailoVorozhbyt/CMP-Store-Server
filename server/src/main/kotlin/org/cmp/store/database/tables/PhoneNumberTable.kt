package org.cmp.store.database.tables

import org.jetbrains.exposed.v1.core.Table

object PhoneNumberTable : Table("phone_numbers") {
    val customerId = varchar("customer_id", 128).references(CustomerTable.id)
    val dialCode = integer("dial_code")
    val number = varchar("number", 32)

    override val primaryKey = PrimaryKey(customerId)
}
