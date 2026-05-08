package com.store.di

import com.feature.authentication.domain.usecases.CreateCustomerUseCase
import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.authentication.domain.usecases.UpdateCustomerUseCase
import com.feature.home.domain.usecases.ReadDiscountedProductsUseCase
import com.feature.home.domain.usecases.ReadNewProductsUseCase
import com.feature.home.domain.usecases.ReadProductByIdUseCase
import com.feature.home.domain.usecases.ReadProductsByCategoryUseCase
import com.feature.home.domain.usecases.ReadProductsByIdsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::CreateCustomerUseCase)
    factoryOf(::GetCurrentUserIdUseCase)
    factoryOf(::ReadCustomerUseCase)
    factoryOf(::UpdateCustomerUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::ReadDiscountedProductsUseCase)
    factoryOf(::ReadNewProductsUseCase)
    factoryOf(::ReadProductByIdUseCase)
    factoryOf(::ReadProductsByCategoryUseCase)
    factoryOf(::ReadProductsByIdsUseCase)
}
