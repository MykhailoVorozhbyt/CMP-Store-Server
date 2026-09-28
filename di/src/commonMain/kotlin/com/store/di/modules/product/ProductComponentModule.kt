package com.store.di.modules.product

import com.store.component.product.data.di.productDataModule
import com.store.component.product.usecase.di.productUseCaseModule
import org.koin.dsl.module

val productComponentModule = module {
    includes(productDataModule, productUseCaseModule)
}
