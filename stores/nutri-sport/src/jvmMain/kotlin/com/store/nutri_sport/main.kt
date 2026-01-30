package com.store.nutri_sport

import androidx.compose.ui.window.application
import com.store.nutri_sport.di.nutriSportThemeModule
import org.cmp.store.DesktopApp

fun main() {
    application {
        DesktopApp(
            onCloseRequest = ::exitApplication,
            title = "Nutri Sport",
            appModules = arrayOf(nutriSportThemeModule)
        )
    }
}