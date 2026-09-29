package org.cmp.store

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.store.di.initializeKoin
import org.cmp.store.di.appViewModelModule
import org.cmp.store.presentation.App
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.core.module.Module

fun desktopApp(
    title: String = "Untitled",
    iconResource: DrawableResource,
    vararg appModules: Module,
) {
    initializeKoin(*appModules, appViewModelModule)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            icon = painterResource(iconResource),
            title = title
        ) {
            App()
        }
    }
}
