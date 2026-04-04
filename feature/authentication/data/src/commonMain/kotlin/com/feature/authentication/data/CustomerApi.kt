package com.feature.authentication.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.cmp.store.domain.customer.Customer

class CustomerApi(private val client: HttpClient) {

    suspend fun createCustomer(customer: Customer) {
        client.post("customer") {
            setBody(customer)
        }
    }

    suspend fun getCustomer(id: String): Customer =
        client.get("customer/$id").body()

    suspend fun updateCustomer(customer: Customer) {
        client.put("customer") {
            setBody(customer)
        }
    }
}
