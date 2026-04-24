package com.store.di

import com.feature.authentication.domain.usecases.CreateCustomerUseCase
import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.authentication.domain.usecases.UpdateCustomerUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {
    singleOf(::CreateCustomerUseCase)
    singleOf(::GetCurrentUserIdUseCase)
    singleOf(::ReadCustomerUseCase)
    singleOf(::UpdateCustomerUseCase)
    singleOf(::SignOutUseCase)
}
