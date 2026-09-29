package com.store.di.modules.auth

import com.store.component.auth.data.di.authenticationDataModule
import com.store.component.auth.usecase.di.authUseCaseModule
import org.koin.dsl.module

val authComponentModule = module {
    includes(authenticationDataModule, authUseCaseModule)
}
