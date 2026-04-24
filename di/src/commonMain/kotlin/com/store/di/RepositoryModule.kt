package com.store.di

import com.feature.authentication.data.CustomerRepositoryImpl
import com.feature.authentication.domain.repository.CustomerRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl(get()) }
}
