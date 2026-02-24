package org.cmp.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import com.store.core.presentation.theme.BaseTheme
import com.store.di.initializeKoin
import org.koin.core.module.Module

@Composable
fun DesktopApp(
    onCloseRequest: () -> Unit, title: String = "Untitled", vararg appModules: Module
) {
    initializeKoin(appModules = appModules)
    Window(
        onCloseRequest = onCloseRequest, title = title
    ) {
        BaseTheme { App() }
    }
}