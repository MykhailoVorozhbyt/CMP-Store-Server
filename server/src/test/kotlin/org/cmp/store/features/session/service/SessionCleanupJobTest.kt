package org.cmp.store.features.session.service

import org.cmp.store.database.dao.AuthSessionDao
import org.cmp.store.database.dao.AuthSessionDaoImpl
import org.cmp.store.database.dao.RefreshTokenDaoImpl
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.session.dto.AuthSessionDto
import org.cmp.store.features.session.dto.RefreshTokenDto
import org.cmp.store.utils.assertFailsWithSuspend
import org.cmp.store.utils.dbQuery
import org.cmp.store.utils.testDaoDatabase
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SessionCleanupJobTest {

    private val authSessionDao = AuthSessionDaoImpl()
    private val refreshTokenDao = RefreshTokenDaoImpl()
    private val job = SessionCleanupJob(authSessionDao, refreshTokenDao)

    @Test
    fun sweep_removes_expired_access_tokens_and_keeps_live_ones() = testDaoDatabase {
        val now = System.currentTimeMillis()
        issueAccessToken(token = "stale", expiresAt = now - 1)
        issueAccessToken(token = "live", expiresAt = now + MINUTE)

        job.sweep()

        assertNull(authSessionDao.find("stale"))
        assertNotNull(authSessionDao.find("live"))
    }

    @Test
    fun sweep_removes_expired_refresh_tokens_and_keeps_live_ones() = testDaoDatabase {
        val now = System.currentTimeMillis()
        val family = "family-mixed"
        plantRefreshToken(family = family, expiresAt = now - 1)
        plantRefreshToken(family = family, expiresAt = now + MINUTE)

        job.sweep()

        assertEquals(1, refreshTokenDao.findByFamily(family).size)
    }

    /**
     * The property the whole reuse-detection scheme rests on. A spent token must stay
     * recognisable for as long as it would otherwise have been valid — delete it early and
     * a stolen copy presented afterwards looks like an unknown token (a plain 401) instead
     * of theft, so the family is never torn down.
     */
    @Test
    fun sweep_keeps_a_revoked_refresh_token_until_it_expires() = testDaoDatabase {
        val now = System.currentTimeMillis()
        val family = "family-revoked"
        plantRefreshToken(
            family = family,
            expiresAt = now + MINUTE,
            revokedAt = now - MINUTE,
            replacedBy = UUID.randomUUID().toString(),
        )

        job.sweep()

        assertEquals(1, refreshTokenDao.findByFamily(family).size)
    }

    @Test
    fun sweep_survives_a_failing_database() = testDaoDatabase {
        val failing = SessionCleanupJob(
            authSessionDao = ThrowingAuthSessionDao { IllegalStateException("SQLITE_BUSY") },
            refreshTokenDao = refreshTokenDao,
        )

        // Losing one round to write contention must not kill the loop — the rows are already
        // unusable and the next sweep retries.
        failing.sweep()
    }

    /**
     * The reason [SessionCleanupJob] cannot use plain `runCatching`: that would swallow
     * cancellation and leave this coroutine ignoring its parent, logging a bogus failure on
     * every clean shutdown.
     */
    @Test
    fun sweep_rethrows_cancellation() = testDaoDatabase {
        val cancelling = SessionCleanupJob(
            authSessionDao = ThrowingAuthSessionDao { CancellationException("shutting down") },
            refreshTokenDao = refreshTokenDao,
        )

        assertFailsWithSuspend<CancellationException> { cancelling.sweep() }
    }

    private suspend fun issueAccessToken(token: String, expiresAt: Long) =
        authSessionDao.issue(
            token = token,
            session = AuthSessionDto(
                customerId = CUSTOMER_ID,
                provider = AuthProvider.MANUAL,
                expiresAt = expiresAt,
                familyId = "family-$token",
            )
        )

    private suspend fun plantRefreshToken(
        family: String,
        expiresAt: Long,
        revokedAt: Long? = null,
        replacedBy: String? = null,
    ): Unit = dbQuery {
        refreshTokenDao.createWithinTransaction(
            RefreshTokenDto(
                id = UUID.randomUUID().toString(),
                tokenHash = SessionTokenFactory.hash(SessionTokenFactory.generate()),
                familyId = family,
                customerId = CUSTOMER_ID,
                provider = AuthProvider.MANUAL,
                expiresAt = expiresAt,
                revokedAt = revokedAt,
                replacedBy = replacedBy,
            )
        )
    }

    private companion object {
        const val CUSTOMER_ID = "cleanup-customer"
        const val MINUTE = 60_000L
    }
}

/**
 * Delegates everything except the sweep query, which is the only call the job makes and so
 * the only one worth failing.
 */
private class ThrowingAuthSessionDao(
    private val delegate: AuthSessionDao = AuthSessionDaoImpl(),
    private val failure: () -> Throwable,
) : AuthSessionDao by delegate {
    override suspend fun deleteExpiredBefore(timestamp: Long): Int = throw failure()
}
