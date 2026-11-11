package com.store.component.customer.data.di

import com.store.component.customer.data.CustomerRepositoryImpl
import com.store.component.customer.data.data_source.CustomerDataSource
import com.store.component.customer.data.data_source.CustomerLocalDataSource
import com.store.component.customer.data.data_source.KtorCustomerDataSource
import com.store.component.customer.data.data_source.RoomCustomerLocalDataSource
import com.store.component.customer.domain_api.CustomerRepository
import com.store.core.security.SessionScopedStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val customerDataModule = module {
    singleOf(::KtorCustomerDataSource).bind<CustomerDataSource>()
    singleOf(::RoomCustomerLocalDataSource)
        .binds(listOf(CustomerLocalDataSource::class, SessionScopedStorage::class))
    singleOf(::CustomerRepositoryImpl).bind<CustomerRepository>()
}
