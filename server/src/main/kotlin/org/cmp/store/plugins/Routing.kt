package org.cmp.store.plugins

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.cmp.store.features.auth.routes.authRoutes
import org.cmp.store.features.customer.routes.customerRoutes
import org.cmp.store.features.product.routes.productRoutes
import org.cmp.store.features.serverRout

fun Application.configureRouting() {
    routing {
        serverRout()
        authRoutes()
        customerRoutes()
        productRoutes()
    }
}