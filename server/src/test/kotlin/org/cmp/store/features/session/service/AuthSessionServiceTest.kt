package org.cmp.store.features.session.service

import io.ktor.http.HttpStatusCode
import org.cmp.store.database.dao.AuthSessionDaoImpl
import org.cmp.store.database.dao.RefreshTokenDaoImpl
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.session.dto.AuthSessionDto
import org.cmp.store.features.session.dto.RefreshTokenDto
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.assertFailsWithSuspend
import org.cmp.store.utils.dbQuery
import org.cmp.store.utils.testDaoDatabase
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthSessionServiceTest {

    private val authSessionDao = AuthSessionDaoImpl()
    private val refreshTokenDao = RefreshTokenDaoImpl()
    private val graceCache = RefreshGraceCache()
    private val authSessionService =
        AuthSessionServiceImpl(authSessionDao, refreshTokenDao, graceCache)

    @Test
    fun issued_token_can_be_read_back() = testDaoDatabase {
        val tokens = authSessionService.issueSession(
            customerId = "customer-1",
            provider = AuthProvider.GOOGLE,
        )

        val session = assertNotNull(authSessionService.readSession(tokens.accessToken))

        assertEquals("customer-1", session.customerId)
        assertEquals(AuthProvider.GOOGLE, session.provider)
    }

    @Test
    fun issued_session_hands_out_two_distinct_tokens() = testDaoDatabase {
        val tokens = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)

        // The refresh token must not double as a bearer credential, or a leaked access
        // token would also grant the ability to mint new ones.
        assertNotEquals(tokens.accessToken, tokens.refreshToken)
    }

    @Test
    fun issued_refresh_token_is_never_stored_in_the_clear() = testDaoDatabase {
        val tokens = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(tokens.accessToken)).familyId

        val stored = refreshTokenDao.findByFamily(familyId).single()

        assertNotEquals(tokens.refreshToken, stored.tokenHash)
        assertEquals(SessionTokenFactory.hash(tokens.refreshToken), stored.tokenHash)
    }

    @Test
    fun revoke_removes_token() = testDaoDatabase {
        val tokens = authSessionService.issueSession(
            customerId = "customer-2",
            provider = AuthProvider.MANUAL,
        )

        authSessionService.revokeSession(tokens.accessToken)

        assertNull(authSessionService.readSession(tokens.accessToken))
    }

    @Test
    fun readSession_returns_null_for_unknown_token() = testDaoDatabase {
        assertNull(authSessionService.readSession("missing-token"))
    }

    /**
     * Expiry is enforced on read, not by a background sweep, so the only way to reach that
     * branch is to plant an already-expired row. Goes through the DAO rather than
     * [AuthSessionServiceImpl.issueSession] because the service always stamps a future expiry.
     */
    @Test
    fun readSession_rejects_and_deletes_expired_token() = testDaoDatabase {
        val token = "expired-token"
        authSessionDao.issue(
            token = token,
            session = AuthSessionDto(
                customerId = "customer-3",
                provider = AuthProvider.MANUAL,
                expiresAt = System.currentTimeMillis() - 1,
                familyId = "family-3",
            ),
        )

        assertNull(authSessionService.readSession(token))
        // Rejecting is not enough — the row must actually be gone, otherwise expired
        // sessions accumulate forever in auth_sessions.
        assertNull(authSessionDao.find(token))
    }

    // ---------- rotation ----------

    @Test
    fun refresh_hands_out_a_fresh_pair() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)

        val second = authSessionService.refreshSession(first.refreshToken)

        assertNotEquals(first.accessToken, second.accessToken)
        assertNotEquals(first.refreshToken, second.refreshToken)
        assertNotNull(authSessionService.readSession(second.accessToken))
    }

    @Test
    fun refresh_carries_over_customer_and_provider() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-9", AuthProvider.GOOGLE)

        val second = authSessionService.refreshSession(first.refreshToken)
        val session = assertNotNull(authSessionService.readSession(second.accessToken))

        // Provider lives only on the refresh row after rotation; losing it would silently
        // downgrade a Google session to MANUAL in CustomerPrincipal.
        assertEquals("customer-9", session.customerId)
        assertEquals(AuthProvider.GOOGLE, session.provider)
    }

    @Test
    fun refresh_keeps_the_rotated_token_in_the_same_family() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(first.accessToken)).familyId

        val second = authSessionService.refreshSession(first.refreshToken)

        assertEquals(familyId, assertNotNull(authSessionService.readSession(second.accessToken)).familyId)
        assertEquals(2, refreshTokenDao.findByFamily(familyId).size)
    }

    @Test
    fun refresh_rejects_an_unknown_token() = testDaoDatabase {
        val error = assertFailsWithSuspend<ApiException> {
            authSessionService.refreshSession("never-issued")
        }

        assertEquals(HttpStatusCode.Unauthorized, error.statusCode)
        assertEquals(NetworkError.INVALID_REFRESH_TOKEN, error.networkError)
    }

    @Test
    fun refresh_rejects_an_expired_token() = testDaoDatabase {
        val raw = plantRefreshToken(
            familyId = "family-expired",
            expiresAt = System.currentTimeMillis() - 1,
        )

        val error = assertFailsWithSuspend<ApiException> {
            authSessionService.refreshSession(raw)
        }

        assertEquals(NetworkError.INVALID_REFRESH_TOKEN, error.networkError)
    }

    // ---------- theft detection ----------

    /**
     * The honest-client case: several requests hit a 401 at once and race into refresh, so
     * the loser arrives holding a token the winner just spent. Treating that as theft would
     * sign the user out of a device that is working fine.
     */
    @Test
    fun reusing_a_just_spent_token_replays_the_winners_pair() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val winner = authSessionService.refreshSession(first.refreshToken)

        val loser = authSessionService.refreshSession(first.refreshToken)

        // The same pair, not a second one. Minting again would fork the family into two
        // chains rotating independently, and reuse detection could never fire on either.
        assertEquals(winner, loser)
        assertNotNull(authSessionService.readSession(winner.accessToken))
    }

    @Test
    fun replaying_a_spent_token_inside_the_window_mints_nothing_new() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(first.accessToken)).familyId
        authSessionService.refreshSession(first.refreshToken)
        val afterRotation = refreshTokenDao.findByFamily(familyId).size

        repeat(5) { authSessionService.refreshSession(first.refreshToken) }

        // Without the replay being idempotent, each of these would have added a row — an
        // attacker holding one stolen token could mint unbounded live sessions.
        assertEquals(afterRotation, refreshTokenDao.findByFamily(familyId).size)
    }

    @Test
    fun logout_beats_a_token_still_sitting_in_the_grace_window() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val rotated = authSessionService.refreshSession(first.refreshToken)

        authSessionService.revokeSession(rotated.accessToken)

        // The replay cache must not outlive the chain it caches: answering from it here would
        // hand back 200 and a pair whose rows the logout already deleted.
        val error = assertFailsWithSuspend<ApiException> {
            authSessionService.refreshSession(first.refreshToken)
        }
        assertEquals(NetworkError.INVALID_REFRESH_TOKEN, error.networkError)
    }

    @Test
    fun reusing_a_long_spent_token_tears_down_the_family() = testDaoDatabase {
        val live = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(live.accessToken)).familyId
        val stolen = plantRefreshToken(
            familyId = familyId,
            revokedAt = System.currentTimeMillis() - MINUTE,
            replacedBy = UUID.randomUUID().toString(),
        )

        val error = assertFailsWithSuspend<ApiException> {
            authSessionService.refreshSession(stolen)
        }

        assertEquals(NetworkError.TOKEN_REUSE_DETECTED, error.networkError)
        // Revoking the refresh chain is pointless while its access tokens still work: they
        // outlive the token that minted them.
        assertNull(authSessionService.readSession(live.accessToken))
        assertFailsWithSuspend<ApiException> { authSessionService.refreshSession(live.refreshToken) }
    }

    /**
     * Guards the interaction between the grace window and deliberate revocation. Revoking a
     * family stamps `revokedAt` as "now", which lands inside the grace window — without
     * telling the two cases apart by [RefreshTokenDto.replacedBy], a logout would hand the
     * session straight back to the next refresh.
     */
    @Test
    fun a_deliberately_revoked_token_gets_no_grace() = testDaoDatabase {
        val tokens = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)

        authSessionService.revokeSession(tokens.accessToken)

        val error = assertFailsWithSuspend<ApiException> {
            authSessionService.refreshSession(tokens.refreshToken)
        }
        assertEquals(NetworkError.INVALID_REFRESH_TOKEN, error.networkError)
    }

    /**
     * The second half of that guard, and the subtler one. `revokeFamily` skips rows that
     * already carry a `revokedAt`, so a token spent by rotation moments earlier keeps both
     * its old timestamp and its successor — it looks exactly like an honest race loser.
     * Rotating it would mint a fresh pair into a family that was just torn down.
     */
    @Test
    fun a_revoked_family_cannot_be_revived_by_a_race_token() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(first.accessToken)).familyId
        // Spend `first` by rotation, so it carries a successor and a very recent revokedAt.
        authSessionService.refreshSession(first.refreshToken)

        authSessionService.revokeSession(first.accessToken)

        val error = assertFailsWithSuspend<ApiException> {
            authSessionService.refreshSession(first.refreshToken)
        }
        assertEquals(NetworkError.INVALID_REFRESH_TOKEN, error.networkError)
        // Nothing new was minted into the dead chain.
        assertTrue(refreshTokenDao.findByFamily(familyId).all { it.revokedAt != null })
    }

    // ---------- invariants the grace rule reads ----------

    /**
     * Rotation must record the successor: the refresh policy uses its presence to tell a
     * spent token from a deliberately killed one. Stop writing it and every honest refresh
     * race would start tearing down families instead of being forgiven.
     */
    @Test
    fun rotation_records_the_successor() = testDaoDatabase {
        val first = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(first.accessToken)).familyId

        authSessionService.refreshSession(first.refreshToken)

        val spent = refreshTokenDao.findByFamily(familyId)
            .single { it.tokenHash == SessionTokenFactory.hash(first.refreshToken) }
        assertNotNull(spent.revokedAt)
        assertNotNull(spent.replacedBy)
    }

    /** The mirror image: a deliberate kill must leave no successor behind. */
    @Test
    fun revocation_records_no_successor() = testDaoDatabase {
        val tokens = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val familyId = assertNotNull(authSessionService.readSession(tokens.accessToken)).familyId

        authSessionService.revokeSession(tokens.accessToken)

        val killed = refreshTokenDao.findByFamily(familyId).single()
        assertNotNull(killed.revokedAt)
        assertNull(killed.replacedBy)
    }

    // ---------- logout ----------

    @Test
    fun logout_leaves_the_customers_other_devices_signed_in() = testDaoDatabase {
        val phone = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)
        val desktop = authSessionService.issueSession("customer-1", AuthProvider.MANUAL)

        authSessionService.revokeSession(phone.accessToken)

        // Each login is its own family, so revocation must not reach across them.
        assertNull(authSessionService.readSession(phone.accessToken))
        assertNotNull(authSessionService.readSession(desktop.accessToken))
        val rotated = authSessionService.refreshSession(desktop.refreshToken)
        assertNotNull(authSessionService.readSession(rotated.accessToken))
    }

    @Test
    fun logout_is_a_no_op_for_an_unknown_token() = testDaoDatabase {
        authSessionService.revokeSession("never-issued")
    }

    /** Returns the raw token; only its hash reaches the database. */
    private suspend fun plantRefreshToken(
        familyId: String,
        customerId: String = "customer-1",
        expiresAt: Long = System.currentTimeMillis() + MINUTE,
        revokedAt: Long? = null,
        replacedBy: String? = null,
    ): String {
        val raw = SessionTokenFactory.generate()
        dbQuery {
            refreshTokenDao.createWithinTransaction(
                RefreshTokenDto(
                    id = UUID.randomUUID().toString(),
                    tokenHash = SessionTokenFactory.hash(raw),
                    familyId = familyId,
                    customerId = customerId,
                    provider = AuthProvider.MANUAL,
                    expiresAt = expiresAt,
                    revokedAt = revokedAt,
                    replacedBy = replacedBy,
                )
            )
        }
        return raw
    }

    private companion object {
        const val MINUTE = 60_000L
    }
}
