package com.store.athletica_plus

import androidx.compose.ui.window.application
import com.store.athletica_plus.theme.di.athleticaPlusThemeModule
import org.cmp.store.DesktopApp


fun main() {
    application {
        DesktopApp(
            onCloseRequest = ::exitApplication,
            title = "Athletica Plus",
            appModules = arrayOf(athleticaPlusThemeModule)
        )
    }
}