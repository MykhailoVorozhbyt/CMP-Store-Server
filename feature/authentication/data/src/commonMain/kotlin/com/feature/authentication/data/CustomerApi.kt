package com.feature.authentication.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.cmp.store.domain.customer.Customer
import org.cmp.store.network.ApiResult
import org.cmp.store.network.EmptyResult
import org.cmp.store.network.NetworkError
import org.cmp.store.network.safeApiCall

class CustomerApi(private val client: HttpClient) {

    suspend fun createCustomer(customer: Customer): EmptyResult<NetworkError> =
        safeApiCall { client.post(CUSTOMER) { setBody(customer) } }

    suspend fun getCustomer(id: String): ApiResult<Customer, NetworkError> =
        safeApiCall { client.get("$CUSTOMER/$id").body() }

    suspend fun updateCustomer(customer: Customer): EmptyResult<NetworkError> =
        safeApiCall { client.put(CUSTOMER) { setBody(customer) } }

    companion object {
        private const val CUSTOMER = "customer"
    }
}
