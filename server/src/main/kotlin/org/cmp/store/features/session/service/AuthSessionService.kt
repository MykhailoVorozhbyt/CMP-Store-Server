package org.cmp.store.features.session.service

import io.ktor.http.HttpStatusCode
import org.cmp.store.database.dao.AuthSessionDao
import org.cmp.store.database.dao.RefreshTokenDao
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.features.session.dto.AuthSessionDto
import org.cmp.store.features.session.models.RefreshOutcome
import org.cmp.store.features.session.models.RefreshResult
import org.cmp.store.features.session.dto.RefreshTokenDto
import org.cmp.store.features.session.models.SessionTokens
import org.cmp.store.network.NetworkError
import org.cmp.store.utils.ApiException
import org.cmp.store.utils.dbQuery
import java.util.UUID

interface AuthSessionService {
    suspend fun issueSession(customerId: String, provider: AuthProvider): SessionTokens
    suspend fun readSession(token: String): AuthSessionDto?
    suspend fun refreshSession(refreshToken: String): SessionTokens
    suspend fun revokeSession(accessToken: String)
}

class AuthSessionServiceImpl(
    private val authSessionDao: AuthSessionDao,
    private val refreshTokenDao: RefreshTokenDao,
    private val graceCache: RefreshGraceCache,
) : AuthSessionService {

    override suspend fun issueSession(
        customerId: String,
        provider: AuthProvider,
    ): SessionTokens = dbQuery {
        mint(
            customerId = customerId,
            provider = provider,
            familyId = UUID.randomUUID().toString(),
            now = System.currentTimeMillis(),
        ).toSessionTokens()
    }

    override suspend fun readSession(token: String): AuthSessionDto? {
        val session = authSessionDao.find(token) ?: return null
        if (session.expiresAt <= System.currentTimeMillis()) {
            authSessionDao.delete(token)
            return null
        }
        return session
    }

    /**
     * Lookup and rotation share one transaction, or two concurrent refreshes both mint.
     *
     * The block returns a verdict rather than throwing: [RefreshResult.Compromised] is reached
     * by writing, and an exception would roll that teardown back.
     */
    override suspend fun refreshSession(refreshToken: String): SessionTokens {
        val tokenHash = SessionTokenFactory.hash(refreshToken)

        // Ahead of the DB: a token spent moments ago is a refresh race, and the answer is the
        // pair it already produced. The transaction would read it as reuse.
        graceCache.replayOrNull(tokenHash)?.let { return it }

        val result = dbQuery {
            val now = System.currentTimeMillis()
            val stored = refreshTokenDao
                .findByTokenHashWithinTransaction(tokenHash)
                ?: return@dbQuery RefreshResult.Rejected

            val familyIsAlive =
                refreshTokenDao.hasActiveTokenInFamilyWithinTransaction(stored.familyId)

            when (decideRefreshOutcome(stored, now, familyIsAlive)) {
                RefreshOutcome.REJECT -> RefreshResult.Rejected

                RefreshOutcome.REVOKE_FAMILY -> {
                    refreshTokenDao.revokeFamilyWithinTransaction(stored.familyId, now)
                    authSessionDao.deleteByFamilyWithinTransaction(stored.familyId)
                    RefreshResult.Compromised(stored.familyId)
                }

                RefreshOutcome.ROTATE -> {
                    val minted = mint(
                        customerId = stored.customerId,
                        provider = stored.provider,
                        familyId = stored.familyId,
                        now = now,
                    )
                    refreshTokenDao.markReplacedWithinTransaction(
                        id = stored.id,
                        replacedBy = minted.refreshTokenId,
                        revokedAt = now,
                    )
                    val tokens = minted.toSessionTokens()
                    graceCache.record(tokenHash, stored.familyId, tokens)
                    RefreshResult.Rotated(tokens)
                }
            }
        }

        return when (result) {
            is RefreshResult.Rotated -> result.tokens

            RefreshResult.Rejected -> throw ApiException(
                HttpStatusCode.Unauthorized,
                NetworkError.INVALID_REFRESH_TOKEN
            )

            is RefreshResult.Compromised -> {
                graceCache.invalidateFamily(result.familyId)
                throw ApiException(
                    HttpStatusCode.Unauthorized,
                    NetworkError.TOKEN_REUSE_DETECTED
                )
            }
        }
    }

    /** Logout: drops this login chain only, leaving the customer's other devices signed in. */
    override suspend fun revokeSession(accessToken: String) {
        val session = authSessionDao.find(accessToken) ?: return
        dbQuery {
            val now = System.currentTimeMillis()
            refreshTokenDao.revokeFamilyWithinTransaction(session.familyId, now)
            authSessionDao.deleteByFamilyWithinTransaction(session.familyId)
        }
        // After the commit: a token this chain rotated moments ago is still in the grace cache,
        // and replaying it must not answer 200 with tokens this logout just deleted.
        graceCache.invalidateFamily(session.familyId)
    }

    /**
     * Classifies a refresh token presented at [now].
     *
     * A set [RefreshTokenDto.revokedAt] means the token was already spent. The honest refresh
     * race is answered by [RefreshGraceCache] before this runs, so anything reaching the last
     * line has been spent for longer than a race lasts and is read as a second holder.
     *
     * @param familyIsAlive whether any token in the chain is still unspent — a property of the
     *   chain, so it cannot be read off [stored].
     */
    private fun decideRefreshOutcome(
        stored: RefreshTokenDto,
        now: Long,
        familyIsAlive: Boolean,
    ): RefreshOutcome {
        // Ahead of revocation on purpose: a token that fell out of a still-rotating family is
        // both spent and expired, and reading that as theft would let a week-old cached token
        // tear down a working session.
        if (stored.expiresAt <= now) return RefreshOutcome.REJECT

        stored.revokedAt ?: return RefreshOutcome.ROTATE

        // No successor means killed deliberately (logout, or a theft response), not spent.
        if (stored.replacedBy == null) return RefreshOutcome.REJECT

        // Spent before the family was revoked, so revokeFamily left it alone. Without this a
        // stale token would tear down a family that is already gone, to no effect.
        if (!familyIsAlive) return RefreshOutcome.REJECT

        // Spent, superseded, and the chain is still running: someone else is holding a copy.
        return RefreshOutcome.REVOKE_FAMILY
    }

    private fun mint(
        customerId: String,
        provider: AuthProvider,
        familyId: String,
        now: Long,
    ): MintedSession {
        val accessToken = SessionTokenFactory.generate()
        val refreshToken = SessionTokenFactory.generate()
        val refreshTokenId = UUID.randomUUID().toString()

        refreshTokenDao.createWithinTransaction(
            RefreshTokenDto(
                id = refreshTokenId,
                tokenHash = SessionTokenFactory.hash(refreshToken),
                familyId = familyId,
                customerId = customerId,
                provider = provider,
                expiresAt = now + REFRESH_TOKEN_LIFETIME_MILLIS,
            )
        )
        authSessionDao.issueWithinTransaction(
            token = accessToken,
            session = AuthSessionDto(
                customerId = customerId,
                provider = provider,
                expiresAt = now + ACCESS_TOKEN_LIFETIME_MILLIS,
                familyId = familyId,
            )
        )
        return MintedSession(accessToken, refreshToken, refreshTokenId)
    }

    /** Carries the new row id, which [mint]'s caller needs to close out the previous token. */
    private data class MintedSession(
        val accessToken: String,
        val refreshToken: String,
        val refreshTokenId: String,
    )

    private fun MintedSession.toSessionTokens(): SessionTokens = SessionTokens(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )

    private companion object {
        const val ACCESS_TOKEN_LIFETIME_MILLIS = 60L * 60 * 1000

        /** Slides forward on every rotation — an idle timeout, not a hard session cap. */
        const val REFRESH_TOKEN_LIFETIME_MILLIS = 7L * 24 * 60 * 60 * 1000
    }
}
