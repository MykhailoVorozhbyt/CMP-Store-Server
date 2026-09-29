package com.store.di.modules.customer

import com.store.component.customer.data.di.customerDataModule
import com.store.component.customer.usecase.di.customerUseCaseModule
import org.koin.dsl.module

val customerComponentModule = module {
    includes(customerDataModule, customerUseCaseModule)
}
