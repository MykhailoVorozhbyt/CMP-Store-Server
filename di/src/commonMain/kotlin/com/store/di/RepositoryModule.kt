package com.store.di

import com.feature.authentication.data.CustomerRepositoryImpl
import com.feature.authentication.domain.repository.CustomerRepository
import com.feature.home.data.ProductRepositoryImpl
import com.feature.home.domain.repository.ProductRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl() }
}
