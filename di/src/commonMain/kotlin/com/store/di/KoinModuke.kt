package com.store.di

import com.feature.authentication.presentation.AuthenticationViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val repositoryModule = module {
}

val viewModelModule = module {
    viewModel<AuthenticationViewModel>()
}

expect val targetModule: Module


fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
    vararg appModules: Module
) {
    startKoin {
        config?.invoke(this)
        modules(
            repositoryModule,
            viewModelModule,
            targetModule,
            dispatcherModule,
            *appModules
        )
    }
}