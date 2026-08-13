package org.cmp.store

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.resources.Resources
import org.cmp.store.database.DatabaseFactory
import org.cmp.store.plugins.configureRouting
import org.cmp.store.plugins.installAuthentication
import org.cmp.store.plugins.installForwardedHeaders
import org.cmp.store.plugins.installKoin
import org.cmp.store.plugins.installRateLimit
import org.cmp.store.plugins.installSerialization
import org.cmp.store.plugins.installSessionCleanup
import org.cmp.store.plugins.installStatusPages
import org.koin.core.KoinKtorApplication
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * An entry point of the application. This function is referenced in the
 * resources/application.conf file inside the ktor.application.modules.
 *
 * For more information about this file: https://ktor.io/docs/configurations.html#configuration-file
 */
@Suppress("UNUSED")
fun Application.main() {
    module()
}

fun Application.module(koinPlugin: ApplicationPlugin<KoinKtorApplication> = Koin) {
    DatabaseFactory.init(DatabaseFactory.from(environment.config))
    installKoin(koinPlugin)
    installSerialization()
    // Before the rate limit: it decides which address a request is counted against.
    installForwardedHeaders()
    // After serialization: the rate limit key deserializes the body to read the email.
    installRateLimit()
    installStatusPages()
    installAuthentication()
    install(Resources)
    installSessionCleanup()
    configureRouting()
}
