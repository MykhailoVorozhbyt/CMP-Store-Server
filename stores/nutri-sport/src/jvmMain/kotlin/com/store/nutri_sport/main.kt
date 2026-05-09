package com.store.nutri_sport

import com.store.nutri_sport.di.nutriSportThemeModule
import org.cmp.store.desktopApp

fun main() {
    desktopApp(
        title = "Nutri Sport",
        appModules = arrayOf(nutriSportThemeModule)
    )
}