package com.store.nutri_sport

import com.store.nutri_sport.di.nutriSportThemeModule
import org.cmp.store.StoreApp

class NutriSportApp : StoreApp(
    nutriSportThemeModule
) {
    override fun onCreate() {
        super.onCreate()
    }
}