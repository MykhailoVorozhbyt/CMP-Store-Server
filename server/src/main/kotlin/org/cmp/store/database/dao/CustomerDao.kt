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
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

object CustomerDao {

    suspend fun create(customer: Customer) = newSuspendedTransaction {
        CustomerTable.insert {
            it[id] = customer.id
            it[firstName] = customer.firstName
            it[lastName] = customer.lastName
            it[email] = customer.email
            it[city] = customer.city
            it[postalCode] = customer.postalCode
            it[address] = customer.address
            it[isAdmin] = customer.isAdmin
        }
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
                PhoneNumber(
                    dialCode = it[PhoneNumberTable.dialCode],
                    number = it[PhoneNumberTable.number]
                )
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

    suspend fun update(customer: Customer): Boolean = newSuspendedTransaction {
        val updated = CustomerTable.update({ CustomerTable.id eq customer.id }) {
            it[firstName] = customer.firstName
            it[lastName] = customer.lastName
            it[email] = customer.email
            it[city] = customer.city
            it[postalCode] = customer.postalCode
            it[address] = customer.address
            it[isAdmin] = customer.isAdmin
        }
        if (updated == 0) return@newSuspendedTransaction false

        PhoneNumberTable.deleteWhere { customerId eq customer.id }
        customer.phoneNumber?.let { phone ->
            PhoneNumberTable.insert {
                it[customerId] = customer.id
                it[dialCode] = phone.dialCode
                it[number] = phone.number
            }
        }

        CartItemTable.deleteWhere { customerId eq customer.id }
        customer.cart.forEach { item ->
            CartItemTable.insert {
                it[id] = item.id
                it[customerId] = customer.id
                it[productId] = item.productId
                it[flavor] = item.flavor
                it[quantity] = item.quantity
            }
        }

        true
    }
}
