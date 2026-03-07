package com.store.nutri_sport.di

import com.store.core.presentation.theme.AppStrings
import com.store.core.presentation.theme.StoreThemeProvider
import com.store.nutri_sport.theme.NutriSportStoreThemeProvider
import com.store.nutri_sport.theme.NutriSportStrings
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val nutriSportThemeModule = module {
    singleOf(::NutriSportStoreThemeProvider).bind<StoreThemeProvider>()
    singleOf(::NutriSportStrings).bind<AppStrings>()
}