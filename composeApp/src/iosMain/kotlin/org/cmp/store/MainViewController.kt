package org.cmp.store

import androidx.compose.ui.window.ComposeUIViewController
import com.store.di.initializeKoin
import org.koin.core.module.Module

fun MainViewController(
    vararg appModules: Module
) = ComposeUIViewController(
    configure = {
        initializeKoin(appModules = appModules)
    }
) { App() }