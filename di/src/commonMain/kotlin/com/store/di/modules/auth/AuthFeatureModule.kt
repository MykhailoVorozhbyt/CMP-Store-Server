package com.store.di.modules.auth

import com.feature.authentication.data.di.authenticationDataModule
import com.feature.authentication.presentation.di.authenticationPresentationModule
import org.koin.dsl.module

val authFeatureModule = module {
    includes(authenticationDataModule, authenticationPresentationModule)
}
