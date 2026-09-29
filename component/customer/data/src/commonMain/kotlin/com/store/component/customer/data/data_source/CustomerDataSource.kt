package com.store.component.customer.data.data_source

import com.store.component.customer.data.dto.CustomerDto
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import com.store.core.network.utils.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.cmp.store.network.NetworkError

internal interface CustomerDataSource {
    suspend fun getCustomer(id: String): ApiResult<CustomerDto, NetworkError>
    suspend fun updateCustomer(customer: CustomerDto): EmptyResult<NetworkError>
}

internal class KtorCustomerDataSource(private val client: HttpClient) : CustomerDataSource {

    override suspend fun getCustomer(id: String): ApiResult<CustomerDto, NetworkError> =
        safeApiCall { client.get("$CUSTOMER/$id") }

    override suspend fun updateCustomer(customer: CustomerDto): EmptyResult<NetworkError> =
        safeApiCall { client.put(CUSTOMER) { setBody(customer) } }

    private companion object {
        const val CUSTOMER = "customer"
    }
}
