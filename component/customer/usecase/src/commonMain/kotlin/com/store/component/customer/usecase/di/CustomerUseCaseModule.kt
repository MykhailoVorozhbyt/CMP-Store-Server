package com.store.component.customer.usecase.di

import com.store.component.customer.usecase.ReadCustomerUseCase
import com.store.component.customer.usecase.UpdateCustomerUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val customerUseCaseModule = module {
    factoryOf(::ReadCustomerUseCase)
    factoryOf(::UpdateCustomerUseCase)
}
