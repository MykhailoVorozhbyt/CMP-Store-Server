package com.store.di

import com.store.di.modules.app.appFeatureModule
import com.store.di.modules.auth.authComponentModule
import com.store.di.modules.auth.authFeatureModule
import com.store.di.modules.core.coreModule
import com.store.di.modules.customer.customerComponentModule
import com.store.di.modules.home.homeFeatureModule
import com.store.di.modules.product.productComponentModule
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val componentModules = module {
    includes(
        authComponentModule,
        customerComponentModule,
        productComponentModule,
    )
}

val featureModules = module {
    includes(
        authFeatureModule,
        homeFeatureModule,
        appFeatureModule,
    )
}

val sharedAppModule = module {
    includes(
        coreModule,
        componentModules,
        featureModules,
    )
}

val sharedModules: List<Module> = listOf(sharedAppModule)
