package org.cmp.store.storage

import org.cmp.store.domain.customer.Customer
import java.util.concurrent.ConcurrentHashMap

object CustomerStorage {
    private val customers = ConcurrentHashMap<String, Customer>()

    fun create(customer: Customer) {
        customers[customer.id] = customer
    }

    fun read(id: String): Customer? = customers[id]

    fun update(customer: Customer): Boolean {
        if (!customers.containsKey(customer.id)) return false
        customers[customer.id] = customer
        return true
    }
}
