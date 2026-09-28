package com.store.component.auth.usecase.di

import com.store.component.auth.usecase.GetCurrentUserIdUseCase
import com.store.component.auth.usecase.RequestGoogleAccountUseCase
import com.store.component.auth.usecase.SignInUseCase
import com.store.component.auth.usecase.SignOutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authUseCaseModule = module {
    factoryOf(::SignInUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::GetCurrentUserIdUseCase)
    factoryOf(::RequestGoogleAccountUseCase)
}
