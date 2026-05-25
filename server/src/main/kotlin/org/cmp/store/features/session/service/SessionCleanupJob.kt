package org.cmp.store.features.session.service

import com.store.core.utils.extension.runCatchingCancellable
import kotlinx.coroutines.delay
import org.cmp.store.database.dao.AuthSessionDao
import org.cmp.store.database.dao.RefreshTokenDao
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.milliseconds

/**
 * Sweeps rows whose `expires_at` has passed. Hourly access-token rotation means an active
 * client leaves roughly two dead rows behind per hour, and nothing else ever removes them:
 * [AuthSessionServiceImpl.readSession] only deletes tokens someone actually presents, so a
 * client that simply stops calling leaves its rows in place forever.
 *
 * Expiry alone is the right cut-off for `refresh_tokens` as well. A revoked row has to stay
 * recognisable for as long as it would otherwise have been usable — that is what lets reuse
 * be told apart from an unknown token — and once past its own expiry it is rejected whether
 * the row is still there or not.
 */
class SessionCleanupJob(
    private val authSessionDao: AuthSessionDao,
    private val refreshTokenDao: RefreshTokenDao,
) {
    private val logger = LoggerFactory.getLogger(SessionCleanupJob::class.java)

    /**
     * Sweeps immediately, then hourly. Delaying first would mean a process restarting more
     * often than the interval never sweeps at all — and that is where rows pile up fastest.
     */
    suspend fun run() {
        while (true) {
            sweep()
            delay(SWEEP_INTERVAL_MILLIS.milliseconds)
        }
    }

    suspend fun sweep() {
        val now = System.currentTimeMillis()
        runCatchingCancellable {
            // Access tokens first: they are the cheaper delete and the ones that pile up
            // fastest, so a failure part-way still makes progress on the bigger table.
            val sessions = authSessionDao.deleteExpiredBefore(now)
            val refreshTokens = refreshTokenDao.deleteExpiredBefore(now)
            logger.info("Session cleanup removed $sessions access and $refreshTokens refresh tokens")
        }.onFailure {
            // SQLite serialises writers, so a sweep can lose to request traffic. Losing one
            // round is harmless — the rows are already unusable and the next sweep retries.
            // Cancellation never reaches here: the wrapper rethrows it so a shutdown stops
            // the loop instead of being logged as a failure.
            logger.warn("Session cleanup sweep failed", it)
        }
    }

    private companion object {
        const val SWEEP_INTERVAL_MILLIS = 60L * 60 * 1000
    }
}
