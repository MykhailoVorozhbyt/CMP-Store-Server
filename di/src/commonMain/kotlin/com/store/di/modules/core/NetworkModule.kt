package com.store.di.modules.core

import com.store.core.network.createHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    singleOf(::createHttpClient)
}
