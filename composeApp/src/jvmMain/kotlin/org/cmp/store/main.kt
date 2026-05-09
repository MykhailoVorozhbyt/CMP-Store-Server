package org.cmp.store

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.store.di.initializeKoin
import org.cmp.store.di.appViewModelModule
import org.cmp.store.presentation.App
import org.koin.core.module.Module

fun desktopApp(
    title: String = "Untitled",
    vararg appModules: Module,
) {
    initializeKoin(*appModules, appViewModelModule)
    application {
        Window(
            onCloseRequest = ::exitApplication, title = title
        ) {
            App()
        }
    }
}
