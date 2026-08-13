package org.cmp.store.utils

import io.ktor.server.plugins.ratelimit.RateLimitConfig
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.RateLimitProviderConfig
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.routing.Route
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Every rate-limited route group, with its budget.
 *
 * Ktor keys a bucket on the provider name and the request key — **not** on the route. Two
 * groups sharing a name therefore share one quota, silently. Listing the names here makes that
 * sharing an explicit decision instead of a typo, the same way the ModulePath enum does for
 * Gradle paths in build-logic.
 */
enum class RateLimitGroup(
    val limitName: RateLimitName,
    val limit: Int,
    val refillPeriod: Duration,
) {
    /** Sign-in and registration — the only place a password is presented. Keyed per email. */
    AUTH_AUTHORIZE(
        limitName = RateLimitName("auth-authorize"),
        limit = 5,
        refillPeriod = 60.seconds,
    ),

    /**
     * Refresh and logout. Separate from [AUTH_AUTHORIZE] because sharing one bucket let a few
     * malformed sign-in bodies stop everyone on that address from refreshing. These present a
     * token rather than a guessable secret, so the budget is looser.
     */
    AUTH_SESSION(
        limitName = RateLimitName("auth-session"),
        limit = 30,
        refillPeriod = 60.seconds,
    )
}

/**
 * Registers [group] with its own budget. [configure] is where the group's `requestKey` goes —
 * leave it out to fall back on Ktor's default key, which is `Unit`, i.e. one bucket shared by
 * every caller on the planet.
 */
fun RateLimitConfig.register(
    group: RateLimitGroup,
    configure: RateLimitProviderConfig.() -> Unit = {},
) = register(group.limitName) {
    rateLimiter(limit = group.limit, refillPeriod = group.refillPeriod)
    configure()
}

/** Applies [group]'s limit to every route declared in [build]. */
fun Route.rateLimited(
    group: RateLimitGroup,
    build: Route.() -> Unit,
): Route = rateLimit(group.limitName, build)
