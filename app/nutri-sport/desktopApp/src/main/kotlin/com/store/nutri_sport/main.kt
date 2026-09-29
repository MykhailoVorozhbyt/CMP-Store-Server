package com.store.nutri_sport

import com.store.nutri_sport.di.nutriSportThemeModule
import com.store.nutri_sport.theme.nutriSportAppIcon
import org.cmp.store.desktopApp

fun main() {
    desktopApp(
        title = "Nutri Sport",
        iconResource = nutriSportAppIcon,
        appModules = arrayOf(nutriSportThemeModule)
    )
}
