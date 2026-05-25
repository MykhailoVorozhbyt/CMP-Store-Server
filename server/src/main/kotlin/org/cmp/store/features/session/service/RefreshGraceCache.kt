package org.cmp.store.features.session.service

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.cmp.store.features.session.models.SessionTokens

/**
 * Makes a refresh idempotent for a short window, so a client whose requests race into refresh
 * gets the winner's pair back instead of a second one. Minting again would fork the family into
 * two chains and reuse detection could never fire on either.
 *
 * In memory, not in the database: refresh tokens are stored only as SHA-256 hashes, so a
 * persisted successor cannot be re-emitted. A restart inside the window turns a race into a
 * false reuse alarm — rare, and it fails closed.
 */
class RefreshGraceCache(
    private val graceMillis: Long = REFRESH_GRACE_MILLIS,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val mutex = Mutex()
    private val entries = mutableMapOf<String, Entry>()
    private var lastPrunedAt = Long.MIN_VALUE

    /** The pair [tokenHash] rotated into if that was within the window; null means "ask the DB". */
    suspend fun replayOrNull(tokenHash: String): SessionTokens? = mutex.withLock {
        val now = clock()
        pruneIfDue(now)

        val entry = entries[tokenHash] ?: return@withLock null
        if (now - entry.mintedAt > graceMillis) {
            // Past the window it is evidence of reuse, and that verdict belongs to the DB.
            entries.remove(tokenHash)
            return@withLock null
        }
        entry.tokens
    }

    /** Remembers what [tokenHash] rotated into, so a replay inside the window can return it. */
    suspend fun record(tokenHash: String, familyId: String, tokens: SessionTokens) =
        mutex.withLock {
            entries[tokenHash] = Entry(
                tokens = tokens,
                familyId = familyId,
                mintedAt = clock(),
            )
        }

    /**
     * Drops everything cached for [familyId]. Without it a logout or reuse teardown would still
     * be answered from here, with tokens whose rows that teardown just deleted.
     */
    suspend fun invalidateFamily(familyId: String) = mutex.withLock {
        entries.values.removeAll { it.familyId == familyId }
    }

    /**
     * Most entries are never replayed, so nothing else would ever remove them. Sweeping is
     * O(n), hence once per window rather than on every refresh — a tighter schedule collects
     * the same rows.
     */
    private fun pruneIfDue(now: Long) {
        if (now - lastPrunedAt < graceMillis) return
        lastPrunedAt = now
        entries.values.removeAll { now - it.mintedAt > graceMillis }
    }

    private data class Entry(
        val tokens: SessionTokens,
        val familyId: String,
        val mintedAt: Long,
    )

    companion object {
        const val REFRESH_GRACE_MILLIS = 10L * 1000
    }
}
