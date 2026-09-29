package com.store.athletica_plus

import com.store.athletica_plus.theme.di.athleticaPlusThemeModule
import com.store.athletica_plus.theme.theme.athleticaPlusAppIcon
import org.cmp.store.desktopApp

fun main() {
    desktopApp(
        title = "Athletica Plus",
        iconResource = athleticaPlusAppIcon,
        appModules = arrayOf(athleticaPlusThemeModule)
    )
}