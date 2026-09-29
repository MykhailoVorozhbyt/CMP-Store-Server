package com.store.athletica_plus.theme.di

import com.store.athletica_plus.theme.theme.AthleticaPlusStoreThemeProvider
import com.store.athletica_plus.theme.theme.AthleticaPlusStrings
import com.store.core.presentation.theme.AppStrings
import com.store.core.presentation.theme.StoreThemeProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val athleticaPlusThemeModule = module {
    singleOf(::AthleticaPlusStoreThemeProvider).bind<StoreThemeProvider>()
    singleOf(::AthleticaPlusStrings).bind<AppStrings>()
}