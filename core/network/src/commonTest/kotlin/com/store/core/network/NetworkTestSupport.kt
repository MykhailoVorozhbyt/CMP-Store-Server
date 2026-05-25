package com.store.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal val testJson = Json { ignoreUnknownKeys = true }

/**
 * A client with only the plumbing [SessionRefreshCall] needs — no `Auth`, so the call can be
 * exercised on its own rather than through a refresh cycle.
 */
internal fun bareTestClient(engine: MockEngine): HttpClient = HttpClient(engine) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(testJson)
    }
}

internal fun jsonHeaders() =
    headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

internal fun MockRequestHandleScope.respondJson(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
) = respond(content = body, status = status, headers = jsonHeaders())

internal fun tokensJson(accessToken: String, refreshToken: String) =
    """{"accessToken":"$accessToken","refreshToken":"$refreshToken"}"""