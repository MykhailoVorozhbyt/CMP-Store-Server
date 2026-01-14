package com.store.athletica_plus_jvm

import androidx.compose.ui.window.application
import org.cmp.store.DesktopApp


fun main() = application {
    DesktopApp(
        onCloseRequest = ::exitApplication,
        title = "Athletica Plus"
    )
}