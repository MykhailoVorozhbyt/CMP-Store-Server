package org.cmp.store.database

import org.cmp.store.database.tables.CartItemTable
import org.cmp.store.database.tables.CustomerTable
import org.cmp.store.database.tables.PhoneNumberTable
import org.cmp.store.database.tables.ProductTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:sqlite:./store.db",
            driver = "org.sqlite.JDBC"
        )
        transaction {
            SchemaUtils.create(CustomerTable, PhoneNumberTable, CartItemTable, ProductTable)
        }
    }
}
