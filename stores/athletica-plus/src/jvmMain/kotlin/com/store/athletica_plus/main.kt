package com.store.athletica_plus

import androidx.compose.ui.window.application
import org.cmp.store.DesktopApp


fun main() = application {
    DesktopApp(
        onCloseRequest = ::exitApplication,
        title = "Athletica Plus"
    )
}