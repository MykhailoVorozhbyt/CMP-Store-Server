package org.cmp.store.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.ApiException

/**
 * Centralized exception handling for every route. Routes throw (ApiException for expected
 * domain errors) or let exceptions propagate; this plugin maps them to responses and logs
 * the unexpected ones, so individual routes don't need their own try/catch blocks.
 */
fun Application.installStatusPages() {
    install(StatusPages) {
        // Expected domain errors — the route already chose the status.
        exception<ApiException> { call, cause ->
            call.respond(cause.statusCode, cause.networkError.name)
        }
        // Malformed/unreadable request body (e.g. invalid JSON in call.receive<T>()).
        exception<BadRequestException> { call, cause ->
            call.application.environment.log.debug(
                "Bad request on ${call.request.uri}: ${cause.message}",
                cause
            )
            call.respond(HttpStatusCode.BadRequest, NetworkError.SERIALIZATION.name)
        }
        // Anything unexpected — log with stack trace, never leak internals to the client.
        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled exception on ${call.request.uri}", cause)
            call.respond(HttpStatusCode.InternalServerError, NetworkError.SERVER_ERROR.name)
        }

        status(HttpStatusCode.TooManyRequests) { call, status ->
            call.respondText(text = NetworkError.TOO_MANY_REQUESTS.name, status = status)
        }
    }
}
