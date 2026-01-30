package com.store.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule = module {

}

expect val targetModule: Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
    vararg appModules: Module
) {
    startKoin {
        config?.invoke(this)
        modules(sharedModule, targetModule, *appModules)
    }
}