package org.cmp.store.database.dao

import org.cmp.store.database.tables.CartItemTable
import org.cmp.store.database.tables.CustomerTable
import org.cmp.store.database.tables.PhoneNumberTable
import org.cmp.store.domain.customer.CartItem
import org.cmp.store.domain.customer.Customer
import org.cmp.store.domain.customer.PhoneNumber
import org.cmp.store.utils.dbQuery
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.UpdateBuilder
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

interface CustomerDao {
    suspend fun exists(id: String): Boolean
    suspend fun create(customer: Customer)
    /**
     * Inserts a customer and its relations using the caller's existing transaction.
     * Use this to compose a customer insert atomically with other writes.
     */
    fun insertWithinTransaction(customer: Customer)
    suspend fun read(id: String): Customer?
    suspend fun findByEmail(email: String): Customer?
    suspend fun update(customer: Customer): Boolean
}

class CustomerDaoImpl : CustomerDao {

    override suspend fun exists(id: String): Boolean = dbQuery {
        CustomerTable
            .selectAll()
            .where { CustomerTable.id eq id }
            .empty().not()
    }

    override suspend fun create(customer: Customer) = dbQuery {
        insertCustomer(customer)
        insertRelations(customer)
    }

    override fun insertWithinTransaction(customer: Customer) {
        insertCustomer(customer)
        insertRelations(customer)
    }

    override suspend fun read(id: String): Customer? = dbQuery {
        CustomerTable
            .selectAll()
            .where { CustomerTable.id eq id }
            .singleOrNull()
            ?.toCustomer()
    }

    override suspend fun findByEmail(email: String): Customer? = dbQuery {
        CustomerTable
            .selectAll()
            .where { CustomerTable.email eq email }
            .singleOrNull()
            ?.toCustomer()
    }

    override suspend fun update(customer: Customer): Boolean = dbQuery {
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
     * Extension function to map Customer properties to Table Update/Insert statements.
     */
    private fun UpdateBuilder<*>.mapFrom(customer: Customer) {
        this[CustomerTable.id] = customer.id
        this[CustomerTable.firstName] = customer.firstName
        this[CustomerTable.lastName] = customer.lastName
        this[CustomerTable.email] = customer.email
        this[CustomerTable.city] = customer.city
        this[CustomerTable.postalCode] = customer.postalCode
        this[CustomerTable.address] = customer.address
    }

    private fun ResultRow.toCustomer(): Customer {
        val customerId = this[CustomerTable.id]
        val phoneRow = PhoneNumberTable
            .selectAll()
            .where { PhoneNumberTable.customerId eq customerId }
            .singleOrNull()

        val cartRows = CartItemTable
            .selectAll()
            .where { CartItemTable.customerId eq customerId }
            .toList()

        return Customer(
            id = customerId,
            firstName = this[CustomerTable.firstName],
            lastName = this[CustomerTable.lastName],
            email = this[CustomerTable.email],
            city = this[CustomerTable.city],
            postalCode = this[CustomerTable.postalCode],
            address = this[CustomerTable.address],
            isAdmin = this[CustomerTable.isAdmin],
            phoneNumber = phoneRow?.let {
                PhoneNumber(it[PhoneNumberTable.dialCode], it[PhoneNumberTable.number])
            },
            cart = cartRows.map {
                CartItem(
                    id = it[CartItemTable.id],
                    productId = it[CartItemTable.productId],
                    flavor = it[CartItemTable.flavor],
                    quantity = it[CartItemTable.quantity]
                )
            }
        )
    }
}
