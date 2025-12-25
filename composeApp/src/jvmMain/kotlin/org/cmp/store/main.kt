package org.cmp.store

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "CMP-Store-Server",
    ) {
        App()
    }
}