package com.store.di

import com.store.di.modules.app.appFeatureModule
import com.store.di.modules.auth.authFeatureModule
import com.store.di.modules.core.coreModule
import com.store.di.modules.home.homeFeatureModule
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedAppModule = module {
    includes(
        coreModule,
        authFeatureModule,
        homeFeatureModule,
        appFeatureModule,
    )
}

val sharedModules: List<Module> = listOf(sharedAppModule)
