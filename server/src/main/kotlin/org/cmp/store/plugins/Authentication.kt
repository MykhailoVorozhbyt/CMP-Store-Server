package org.cmp.store.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.bearer
import org.cmp.store.features.auth.models.CustomerPrincipal
import org.cmp.store.features.session.service.AuthSessionService
import org.koin.ktor.ext.inject

const val AUTH_TOKEN_PROVIDER = "auth-token"

/**
 * For more information about this file: https://ktor.io/docs/server-bearer-auth.html
 * */
fun Application.installAuthentication() {
    val authSessionService by inject<AuthSessionService>()

    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication) {
        bearer(AUTH_TOKEN_PROVIDER) {
            realm = myRealm
            authenticate { tokenCredential ->
                val session = authSessionService.readSession(tokenCredential.token)
                    ?: return@authenticate null
                CustomerPrincipal(
                    customerId = session.customerId,
                    provider = session.provider,
                    accessToken = tokenCredential.token,
                )
            }
        }
    }
}
