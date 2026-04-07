package com.feature.authentication.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.cmp.store.domain.customer.Customer

class CustomerApi(private val client: HttpClient) {

    suspend fun createCustomer(customer: Customer) {
        client.post(CUSTOMER) {
            setBody(customer)
        }
    }

    suspend fun getCustomer(id: String): Customer =
        client.get("$CUSTOMER/$id").body()

    suspend fun updateCustomer(customer: Customer) {
        client.put(CUSTOMER) {
            setBody(customer)
        }
    }

    companion object {
        private const val CUSTOMER = "customer"
    }
}
