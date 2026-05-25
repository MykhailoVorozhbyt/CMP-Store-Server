package com.store.athletica_plus

import com.store.athletica_plus.theme.di.athleticaPlusThemeModule
import org.cmp.store.desktopApp


fun main() {
    desktopApp(
        title = "Athletica Plus",
        appModules = arrayOf(athleticaPlusThemeModule)
    )
}