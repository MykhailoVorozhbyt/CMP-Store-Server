package com.feature.authentication.data.oauth

import com.feature.authentication.data.model.AuthCallback
import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.data.model.LocalCallbackServer
import com.feature.authentication.data.oauth.GoogleOAuthConfig.CALLBACK_PATH
import com.feature.authentication.data.oauth.GoogleOAuthConfig.REDIRECT_HOST
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

class LocalOAuthCallbackServer {

    internal fun createCallback(expectedState: String): LocalCallbackServer {
        val callbackFuture = CompletableFuture<AuthCallback>()
        val server = HttpServer.create(InetSocketAddress(REDIRECT_HOST, 0), 0)
        val port = server.address.port

        server.createContext(CALLBACK_PATH) { exchange ->
            val params = exchange.requestURI.rawQuery.orEmpty().parseQueryParams()
            val callbackState = params["state"]
            val code = params["code"]
            val error = params["error"]
            val errorDescription = params["error_description"]

            when {
                error != null -> {
                    exchange.respondText("Google sign-in failed: $error")
                    callbackFuture.completeExceptionally(
                        GoogleSignInError.AuthorizationDenied(errorDescription ?: error)
                    )
                }

                callbackState != expectedState -> {
                    exchange.respondText("Google sign-in failed: invalid state.")
                    callbackFuture.completeExceptionally(GoogleSignInError.InvalidState())
                }

                code.isNullOrBlank() -> {
                    exchange.respondText("Google sign-in failed: authorization code is missing.")
                    callbackFuture.completeExceptionally(GoogleSignInError.MissingAuthorizationCode())
                }

                else -> {
                    exchange.respondText("Authorization is complete. You can close this window and return to the app.")
                    callbackFuture.complete(AuthCallback(code = code))
                }
            }
        }

        return LocalCallbackServer(server = server, callbackFuture = callbackFuture, port = port)
    }

    private fun HttpExchange.respondText(body: String) {
        val bytes = body.toByteArray(StandardCharsets.UTF_8)
        responseHeaders.add("Content-Type", "text/plain; charset=utf-8")
        sendResponseHeaders(200, bytes.size.toLong())
        responseBody.use { it.write(bytes) }
    }

    private fun String.parseQueryParams(): Map<String, String> =
        split("&")
            .filter { it.isNotBlank() }
            .associate { entry ->
                val parts = entry.split("=", limit = 2)
                URLDecoder.decode(parts[0], StandardCharsets.UTF_8) to
                        URLDecoder.decode(parts.getOrElse(1) { "" }, StandardCharsets.UTF_8)
            }

}