package com.feature.authentication.data.model

import com.sun.net.httpserver.HttpServer
import java.util.concurrent.CompletableFuture

internal data class LocalCallbackServer(
    val server: HttpServer,
    val callbackFuture: CompletableFuture<AuthCallback>,
    val port: Int,
) {
    fun start() {
        server.start()
    }

    fun stop() {
        server.stop(0)
    }
}