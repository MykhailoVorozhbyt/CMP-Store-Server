package com.store.di.modules.home

import com.feature.home.data.di.homeDataModule
import com.feature.home.presentation.di.homePresentationModule
import org.koin.dsl.module

val homeFeatureModule = module {
    includes(homeDataModule, homePresentationModule)
}
