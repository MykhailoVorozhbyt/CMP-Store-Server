package com.feature.authentication.data.data_source

import io.ktor.client.*
import io.ktor.client.request.*
import com.feature.authentication.data.model.CustomerDto
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import com.store.core.network.utils.safeApiCall
import org.cmp.store.network.NetworkError

interface CustomerDataSource {
    suspend fun getCustomer(id: String): ApiResult<CustomerDto, NetworkError>
    suspend fun updateCustomer(customer: CustomerDto): EmptyResult<NetworkError>
}

class KtorCustomerDataSource(private val client: HttpClient) : CustomerDataSource {

    override suspend fun getCustomer(id: String): ApiResult<CustomerDto, NetworkError> =
        safeApiCall { client.get("$CUSTOMER/$id") }

    override suspend fun updateCustomer(customer: CustomerDto): EmptyResult<NetworkError> =
        safeApiCall { client.put(CUSTOMER) { setBody(customer) } }

    companion object {
        private const val CUSTOMER = "customer"
    }
}
