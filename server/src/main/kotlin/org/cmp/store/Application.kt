package org.cmp.store

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import org.cmp.store.database.dao.ProductDao
import org.cmp.store.database.DatabaseFactory
import org.cmp.store.domain.product.Product
import org.cmp.store.domain.product.ProductCategory
import org.cmp.store.plugins.configureRouting
import org.cmp.store.plugins.configureSerialization
import org.cmp.store.features.customer.customerRoutes
import org.cmp.store.features.product.productRoutes

fun main() {
    DatabaseFactory.init()
    embeddedServer(
        factory = Netty,
        port = SERVER_PORT,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    configureSerialization()
    routing {
        configureRouting()
        customerRoutes()
        productRoutes()
    }
}
