package com.store.feature.home.di

import com.store.feature.home.HomeGraphScreen
import com.store.feature.home.HomeGraphViewModel
import com.store.feature.home.view_data.HomeGraphInitializer
import com.store.core.navigation.di.navEntry
import com.store.core.presentation.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val homePresentationModule = module {
    factoryOf(::HomeGraphInitializer)
    viewModelOf(::HomeGraphViewModel)

    navEntry(Screen.HomeGraph.serializer()) {
        HomeGraphScreen(welcomeMessage = it.welcomeMessage)
    }
}
