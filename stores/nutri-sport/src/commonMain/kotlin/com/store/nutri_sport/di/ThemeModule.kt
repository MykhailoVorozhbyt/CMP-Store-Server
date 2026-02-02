package com.store.nutri_sport.di

import com.store.core.presentation.theme.color.StoreThemeProvider
import com.store.nutri_sport.color.NutriSportStoreThemeProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val nutriSportThemeModule = module {
    singleOf(::NutriSportStoreThemeProvider).bind<StoreThemeProvider>()
}