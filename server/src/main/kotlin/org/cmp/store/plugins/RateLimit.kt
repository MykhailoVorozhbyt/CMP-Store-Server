package org.cmp.store.plugins

import com.store.core.utils.extension.runCatchingCancellable
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.request.receive
import org.cmp.store.features.auth.dto.AuthRequestDto
import org.cmp.store.utils.RateLimitGroup
import org.cmp.store.utils.register
import java.security.MessageDigest
import java.util.Base64
import kotlin.time.Duration.Companion.seconds

/**
 * Throttles the whole `/auth` tree — the only place where credentials are presented, and so
 * the only place worth guessing at. Untrottled, `/auth/authorize` takes passwords as fast as
 * the network allows and `/auth/refresh` takes stolen refresh tokens the same way.
 *
 * The two `/auth` groups are separate buckets on purpose; see [RateLimitGroup.AUTH_SESSION].
 */
fun Application.installRateLimit() {
    install(RateLimit) {
        global {
            rateLimiter(limit = 120, refillPeriod = 60.seconds)
            requestKey { call -> call.request.origin.remoteAddress }
        }
        register(RateLimitGroup.AUTH_AUTHORIZE) {
            requestKey { call -> authorizeRateLimitKey(call) }
        }
        register(RateLimitGroup.AUTH_SESSION) {
            requestKey { call -> call.request.origin.remoteAddress }
        }
    }
}

/**
 * Bucket key for `/auth/authorize`: guesses against one account get their own budget, so a
 * shared NAT does not turn one attacked user into an outage for everyone on that address. An
 * unparseable body falls back to a per-address bucket.
 *
 * Reading the body here is what `DoubleReceive` on the authorize route pays for — the limiter
 * evaluates its key before the route runs.
 */
internal suspend fun authorizeRateLimitKey(call: ApplicationCall): Any {
    val ip = call.request.origin.remoteAddress
    val email = call.runCatchingCancellable { receive<AuthRequestDto>() }
        .getOrNull()
        ?.email
        ?.trim()
        ?.lowercase()
        ?.takeIf { it.isNotEmpty() }

    return ip to email?.let(::fingerprint)
}

/**
 * The limiter only compares keys for equality, so a digest works as well as the raw address and
 * keeps user emails out of the heap, dumps and logs for the length of the window.
 */
private fun fingerprint(value: String): String = Base64.getUrlEncoder()
    .withoutPadding()
    .encodeToString(
        MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
    )
