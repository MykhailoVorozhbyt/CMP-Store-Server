package org.cmp.store.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.install
import org.cmp.store.di.daoModule
import org.cmp.store.di.serviceModule
import org.koin.core.KoinKtorApplication
import org.koin.ktor.plugin.Koin

fun Application.installKoin(koinPlugin: ApplicationPlugin<KoinKtorApplication> = Koin) {
    install(koinPlugin) {
        modules(daoModule, serviceModule)
        // This line is useful for allowing injection of the Application instance itself
        koin.declare(this@installKoin)
    }
}