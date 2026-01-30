package org.cmp.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window

@Composable
fun DesktopApp(
    onCloseRequest: () -> Unit,
    title: String = "Untitled",
) {
    Window(
        onCloseRequest = onCloseRequest,
        title = title
    ) {
        App()
    }
}