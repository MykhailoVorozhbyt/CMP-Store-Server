package com.feature.authentication.domain.repository

import kotlinx.coroutines.flow.Flow
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

interface CustomerRepository {
    suspend fun getCurrentUserId(): String?
    suspend fun getCurrentAccessToken(): String?
    fun readCustomerFlow(): Flow<ApiResult<Customer, NetworkError>>
    suspend fun updateCustomer(customer: Customer): EmptyResult<NetworkError>
}
