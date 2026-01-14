package org.cmp.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import cmp_store_server.composeapp.generated.resources.Res
import cmp_store_server.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DesktopApp(
    onCloseRequest: () -> Unit,
    title: String = "Untitled",
    icon: DrawableResource? = null,
) {
    Window(
        onCloseRequest = onCloseRequest,
        title = title,
        icon = painterResource(icon ?: Res.drawable.compose_multiplatform),
    ) {
        App()
    }
}