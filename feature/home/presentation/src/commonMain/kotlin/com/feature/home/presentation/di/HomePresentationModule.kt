package com.feature.home.presentation.di

import com.feature.home.domain.usecases.ReadDiscountedProductsUseCase
import com.feature.home.domain.usecases.ReadNewProductsUseCase
import com.feature.home.domain.usecases.ReadProductByIdUseCase
import com.feature.home.domain.usecases.ReadProductsByCategoryUseCase
import com.feature.home.domain.usecases.ReadProductsByIdsUseCase
import com.feature.home.presentation.HomeGraphScreen
import com.feature.home.presentation.HomeGraphViewModel
import com.feature.home.presentation.view_data.HomeGraphInitializer
import com.store.core.navigation.di.navEntry
import com.store.core.presentation.navigation.Screen
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@OptIn(KoinExperimentalAPI::class)
val homePresentationModule = module {
    factoryOf(::ReadDiscountedProductsUseCase)
    factoryOf(::ReadNewProductsUseCase)
    factoryOf(::ReadProductByIdUseCase)
    factoryOf(::ReadProductsByCategoryUseCase)
    factoryOf(::ReadProductsByIdsUseCase)

    factoryOf(::HomeGraphInitializer)
    viewModelOf(::HomeGraphViewModel)

    navEntry(Screen.HomeGraph.serializer()) {
        HomeGraphScreen(welcomeMessage = it.welcomeMessage)
    }
}
