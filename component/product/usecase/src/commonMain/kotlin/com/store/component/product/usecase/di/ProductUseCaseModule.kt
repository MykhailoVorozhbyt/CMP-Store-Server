package com.store.component.product.usecase.di

import com.store.component.product.usecase.ReadDiscountedProductsUseCase
import com.store.component.product.usecase.ReadNewProductsUseCase
import com.store.component.product.usecase.ReadProductByIdUseCase
import com.store.component.product.usecase.ReadProductsByCategoryUseCase
import com.store.component.product.usecase.ReadProductsByIdsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val productUseCaseModule = module {
    factoryOf(::ReadDiscountedProductsUseCase)
    factoryOf(::ReadNewProductsUseCase)
    factoryOf(::ReadProductByIdUseCase)
    factoryOf(::ReadProductsByIdsUseCase)
    factoryOf(::ReadProductsByCategoryUseCase)
}
