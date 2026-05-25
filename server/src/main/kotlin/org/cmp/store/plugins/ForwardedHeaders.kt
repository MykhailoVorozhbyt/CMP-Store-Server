package org.cmp.store.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders

private const val BEHIND_PROXY_KEY = "ktor.deployment.behindProxy"

/**
 * Makes `call.request.origin` report the real client address instead of the proxy's.
 *
 * Rate limiting keys on that address, so behind nginx or Cloudflare without this plugin every
 * user in the world shares one bucket — the whole limit collapses into a single counter.
 *
 * Deliberately opt-in: `X-Forwarded-For` is just a request header, so with no proxy in front
 * any client could forge it and mint itself an unlimited supply of fresh quotas. Enabling it
 * is only safe once something we control is guaranteed to overwrite that header.
 */
fun Application.installForwardedHeaders() {
    val behindProxy = environment.config
        .propertyOrNull(BEHIND_PROXY_KEY)
        ?.getString()
        ?.toBooleanStrictOrNull()
        ?: false

    if (!behindProxy) return

    install(XForwardedHeaders) {
        // The last entry is the one our own proxy appended; everything before it was supplied
        // by the caller and is therefore forgeable.
        useLastProxy()
    }
}
