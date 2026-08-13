package com.store.core.network.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.clearAuthTokens

/**
 * Drops the tokens the auth plugin is holding in memory, so the next request reloads them.
 *
 * Keeps the `ktor-client-auth` dependency inside this module — callers only see an `HttpClient`.
 *
 * Call it **after** clearing token storage: reloading happens on the next request, and clearing
 * first would just pull the discarded token back in.
 */
fun HttpClient.clearBearerTokenCache() = clearAuthTokens()
