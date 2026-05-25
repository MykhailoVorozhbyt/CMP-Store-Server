package org.cmp.store.plugins

import io.ktor.server.application.Application
import kotlinx.coroutines.launch
import org.cmp.store.features.session.service.SessionCleanupJob
import org.koin.ktor.ext.inject

/**
 * Launched on the application's own scope, so the sweep loop is cancelled with the server
 * rather than needing a shutdown hook of its own.
 */
fun Application.installSessionCleanup() {
    val sessionCleanupJob by inject<SessionCleanupJob>()
    launch { sessionCleanupJob.run() }
}
