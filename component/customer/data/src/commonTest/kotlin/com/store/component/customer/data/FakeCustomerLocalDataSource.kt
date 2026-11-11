package com.store.component.customer.data

import com.store.component.customer.data.data_source.CustomerLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.cmp.store.domain.customer.Customer

internal class FakeCustomerLocalDataSource : CustomerLocalDataSource {
    val stored = MutableStateFlow<Customer?>(null)
    var clearCalled = false

    override suspend fun getCustomer(): Customer? = stored.value

    override fun observeCustomer(): Flow<Customer?> = stored

    override suspend fun update(customer: Customer) {
        stored.value = customer
    }

    override suspend fun clear() {
        clearCalled = true
        stored.value = null
    }
}
