package com.store.test.network

import com.store.core.network.SERVER_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.cmp.store.SERVER_PORT

val testJson = Json { ignoreUnknownKeys = true }

fun testHttpClient(engine: MockEngine): HttpClient = HttpClient(engine) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(testJson)
    }
    install(DefaultRequest) {
        port = SERVER_PORT
        url(SERVER_BASE_URL)
        contentType(ContentType.Application.Json)
    }
}

inline fun <reified T> MockRequestHandleScope.respondJson(value: T) = respond(
    content = testJson.encodeToString(value),
    status = HttpStatusCode.OK,
    headers = jsonHeaders(),
)

fun jsonHeaders() = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
