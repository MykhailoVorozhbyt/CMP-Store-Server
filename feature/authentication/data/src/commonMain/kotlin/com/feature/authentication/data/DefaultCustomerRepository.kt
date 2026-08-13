package com.feature.authentication.data

import com.feature.authentication.data.data_source.CustomerDataSource
import com.feature.authentication.data.mappers.toCustomer
import com.feature.authentication.data.mappers.toDto
import com.feature.authentication.domain.repository.CustomerRepository
import com.store.core.security.LocalAuthSessionDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.cmp.store.domain.customer.Customer
import com.store.core.domain.ApiResult
import com.store.core.domain.EmptyResult
import com.store.core.domain.mapSuccess
import org.cmp.store.network.NetworkError

class DefaultCustomerRepository(
    private val api: CustomerDataSource,
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
) : CustomerRepository {

    override suspend fun getCurrentUserId(): String? = localAuthSessionDataSource.currentUserId()

    override suspend fun getCurrentAccessToken(): String? =
        localAuthSessionDataSource.currentAccessToken()

    override fun readCustomerFlow(): Flow<ApiResult<Customer, NetworkError>> = flow {
        val id = getCurrentUserId()
        if (id == null) {
            emit(ApiResult.Error(NetworkError.UNAUTHORIZED))
            return@flow
        }
        emit(api.getCustomer(id).mapSuccess { it.toCustomer() })
    }

    override suspend fun updateCustomer(customer: Customer): EmptyResult<NetworkError> =
        api.updateCustomer(customer.toDto())
}
