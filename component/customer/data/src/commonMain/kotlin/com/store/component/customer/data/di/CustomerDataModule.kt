package com.store.component.customer.data.di

import com.store.component.customer.data.DefaultCustomerRepository
import com.store.component.customer.data.data_source.CustomerDataSource
import com.store.component.customer.data.data_source.KtorCustomerDataSource
import com.store.component.customer.domain_api.CustomerRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val customerDataModule = module {
    singleOf(::KtorCustomerDataSource).bind<CustomerDataSource>()
    singleOf(::DefaultCustomerRepository).bind<CustomerRepository>()
}
