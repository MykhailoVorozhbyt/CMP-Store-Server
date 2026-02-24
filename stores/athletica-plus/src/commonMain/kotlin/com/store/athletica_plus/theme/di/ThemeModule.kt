package com.store.athletica_plus.theme.di

import com.store.athletica_plus.theme.color.AthleticaPlusStoreThemeProvider
import com.store.core.presentation.theme.StoreThemeProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val athleticaPlusThemeModule = module {
    singleOf(::AthleticaPlusStoreThemeProvider).bind<StoreThemeProvider>()
}