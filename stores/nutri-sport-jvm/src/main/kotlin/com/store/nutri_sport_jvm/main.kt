package com.store.nutri_sport_jvm

import androidx.compose.ui.window.application
import org.cmp.store.DesktopApp

fun main() = application {
    DesktopApp(
        onCloseRequest = ::exitApplication,
        title = "Nutri Sport"
    )
}