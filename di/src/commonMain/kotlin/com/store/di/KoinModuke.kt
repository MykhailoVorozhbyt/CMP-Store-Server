package com.store.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

expect val targetModule: Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
    vararg appModules: Module
) {
    startKoin {
        config?.invoke(this)
        modules(
            targetModule,
            networkModule,
            repositoryModule,
            useCaseModule,
            dispatchersModule,
            viewModelModule,
            navigationModule,
            *appModules
        )
    }
}
