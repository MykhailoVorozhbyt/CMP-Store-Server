package org.cmp.store.features.auth.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.plugins.doublereceive.DoubleReceive
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.cmp.store.features.auth.dto.AuthRequestDto
import org.cmp.store.features.auth.dto.RefreshRequestDto
import org.cmp.store.features.auth.mappers.toAuthRequest
import org.cmp.store.features.auth.mappers.toDto
import org.cmp.store.features.auth.models.CustomerPrincipal
import org.cmp.store.features.auth.service.AuthService
import org.cmp.store.features.session.service.AuthSessionService
import org.cmp.store.network.NetworkError
import org.cmp.store.plugins.AUTH_TOKEN_PROVIDER
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.Auth
import org.cmp.store.utils.RateLimitGroup
import org.cmp.store.utils.rateLimited
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService by inject<AuthService>()
    val authSessionService by inject<AuthSessionService>()
    rateLimited(RateLimitGroup.AUTH_AUTHORIZE) {
        /**
         * DoubleReceive hooks the received pipeline, so it is in place for the key regardless
         * of where RateLimit sits in the call pipeline.
         **/
        install(DoubleReceive)

        post<Auth.Authorize> {
            val request = call.receive<AuthRequestDto>().toAuthRequest()
            val response = authService.authorize(request)
            call.respond(response.toDto())
        }
    }

    rateLimited(RateLimitGroup.AUTH_SESSION) {
        post<Auth.Refresh> {
            val request = call.receive<RefreshRequestDto>()
            val tokens = authSessionService.refreshSession(request.refreshToken)
            call.respond(tokens.toDto())
        }

        authenticate(AUTH_TOKEN_PROVIDER) {
            post<Auth.Logout> {
                val principal = call.principal<CustomerPrincipal>()
                    ?: throw ApiException(HttpStatusCode.Unauthorized, NetworkError.UNAUTHORIZED)
                authSessionService.revokeSession(principal.accessToken)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
