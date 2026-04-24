package org.cmp.store.database.tables

import org.jetbrains.exposed.sql.Table

object CustomerTable : Table("customers") {
    val id = varchar("id", 128)
    val firstName = varchar("first_name", 128)
    val lastName = varchar("last_name", 128)
    val email = varchar("email", 255)
    val city = varchar("city", 128).nullable()
    val postalCode = integer("postal_code").nullable()
    val address = varchar("address", 255).nullable()
    val isAdmin = bool("is_admin").default(false)

    override val primaryKey = PrimaryKey(id)
}
