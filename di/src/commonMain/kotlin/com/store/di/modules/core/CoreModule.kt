package com.store.di.modules.core

import com.store.di.platformModule
import org.koin.dsl.module

val coreModule = module {
    includes(
        platformModule,
        dispatchersModule,
        networkModule,
    )
}
