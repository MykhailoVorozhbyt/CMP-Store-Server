package org.cmp.store.features

import io.ktor.server.resources.get
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import org.cmp.store.Greeting
import org.cmp.store.utils.ServerStatus


fun Route.serverRout() {
    get<ServerStatus> {
        call.respondText("Ktor: ${Greeting().greet()}")
    }
}