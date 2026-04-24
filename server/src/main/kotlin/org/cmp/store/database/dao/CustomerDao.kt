package org.cmp.store.database.dao

import org.cmp.store.database.tables.CartItemTable
import org.cmp.store.database.tables.CustomerTable
import org.cmp.store.database.tables.PhoneNumberTable
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

object CustomerDao {

    suspend fun exists(id: String): Boolean = newSuspendedTransaction {
        CustomerTable
            .selectAll()
            .where { CustomerTable.id eq id }
            .empty().not()
    }

    suspend fun create(customer: Customer) = newSuspendedTransaction {
        insertCustomer(customer)
        insertRelations(customer)
    }

    suspend fun read(id: String): Customer? = newSuspendedTransaction {
        val customerRow = CustomerTable
            .selectAll()
            .where { CustomerTable.id eq id }
            .singleOrNull() ?: return@newSuspendedTransaction null

        val phoneRow = PhoneNumberTable
            .selectAll()
            .where { PhoneNumberTable.customerId eq id }
            .singleOrNull()

        val cartRows = CartItemTable
            .selectAll()
            .where { CartItemTable.customerId eq id }
            .toList()

        Customer(
            id = customerRow[CustomerTable.id],
            firstName = customerRow[CustomerTable.firstName],
            lastName = customerRow[CustomerTable.lastName],
            email = customerRow[CustomerTable.email],
            city = customerRow[CustomerTable.city],
            postalCode = customerRow[CustomerTable.postalCode],
            address = customerRow[CustomerTable.address],
            isAdmin = customerRow[CustomerTable.isAdmin],
            phoneNumber = phoneRow?.let {
                PhoneNumber(it[PhoneNumberTable.dialCode], it[PhoneNumberTable.number])
            },
            cart = cartRows.map {
                CartItem(
                    it[CartItemTable.id],
                    it[CartItemTable.productId],
                    it[CartItemTable.flavor],
                    it[CartItemTable.quantity]
                )
            }
        )
    }

    suspend fun update(customer: Customer): Boolean = newSuspendedTransaction {
        val updated = CustomerTable.update({ CustomerTable.id eq customer.id }) {
            it.mapFrom(customer)
        }
        if (updated > 0) {
            // Delete old relations and re-insert (Cleanest way to handle nested updates)
            PhoneNumberTable.deleteWhere { customerId eq customer.id }
            CartItemTable.deleteWhere { customerId eq customer.id }
            insertRelations(customer)
            true
        } else false
    }

    // --- Private Helpers to Eliminate Duplication ---
    private fun insertCustomer(customer: Customer) {
        CustomerTable.insert { it.mapFrom(customer) }
    }

    private fun insertRelations(customer: Customer) {
        customer.phoneNumber?.let { phone ->
            PhoneNumberTable.insert {
                it[customerId] = customer.id
                it[dialCode] = phone.dialCode
                it[number] = phone.number
            }
        }
        customer.cart.forEach { item ->
            CartItemTable.insert {
                it[id] = item.id
                it[customerId] = customer.id
                it[productId] = item.productId
                it[flavor] = item.flavor
                it[quantity] = item.quantity
            }
        }
    }

    /**
     * Extension function to map Customer properties to Table Update/Insert statements
     */
    private fun UpdateBuilder<*>.mapFrom(customer: Customer) {
        this[CustomerTable.id] = customer.id
        this[CustomerTable.firstName] = customer.firstName
        this[CustomerTable.lastName] = customer.lastName
        this[CustomerTable.email] = customer.email
        this[CustomerTable.city] = customer.city
        this[CustomerTable.postalCode] = customer.postalCode
        this[CustomerTable.address] = customer.address
        this[CustomerTable.isAdmin] = customer.isAdmin
    }
}
