package org.cmp.store.network

import io.ktor.client.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.cmp.store.SERVER_PORT

fun createHttpClient(): HttpClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(DefaultRequest) {
        port = SERVER_PORT
        url(SERVER_BASE_URL)
        contentType(ContentType.Application.Json)
    }
}
