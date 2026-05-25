package com.store.di.modules.app

import com.store.di.modules.appNavigationModule
import org.koin.dsl.module

val appFeatureModule = module {
    includes(appNavigationModule)
}
