package com.store.di.modules.auth

import com.store.feature.authentication.di.authenticationPresentationModule
import org.koin.dsl.module

val authFeatureModule = module {
    includes(authenticationPresentationModule)
}
