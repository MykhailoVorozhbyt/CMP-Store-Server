package com.store.di.modules.home

import com.store.feature.home.di.homePresentationModule
import org.koin.dsl.module

val homeFeatureModule = module {
    includes(homePresentationModule)
}
