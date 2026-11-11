package com.store.component.customer.data.data_source

import com.store.component.customer.data.mapper.toCartEntities
import com.store.component.customer.data.mapper.toCustomer
import com.store.component.customer.data.mapper.toEntity
import com.store.core.data.database.customer.CustomerDao
import com.store.core.security.SessionScopedStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.cmp.store.domain.customer.Customer

internal interface CustomerLocalDataSource : SessionScopedStorage {
    suspend fun getCustomer(): Customer?
    fun observeCustomer(): Flow<Customer?>
    suspend fun update(customer: Customer)
}

internal class RoomCustomerLocalDataSource(private val dao: CustomerDao) : CustomerLocalDataSource {

    override suspend fun getCustomer(): Customer? = dao.getCustomer()?.toCustomer()

    override fun observeCustomer(): Flow<Customer?> = dao.observeCustomer().map { it?.toCustomer() }

    override suspend fun update(customer: Customer) {
        dao.replace(customer.toEntity(), customer.toCartEntities())
    }

    override suspend fun clear() {
        dao.clear()
    }
}
