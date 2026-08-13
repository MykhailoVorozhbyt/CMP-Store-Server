package org.cmp.store.features.session.models

/**
 * What a refresh transaction settled on, so the caller can fail *after* it commits: reaching
 * [Compromised] means writing, and throwing from inside would roll that teardown back.
 */
sealed interface RefreshResult {

    /**
     * Rotation succeeded; [tokens] are the freshly minted pair. The grace cache is written inside
     * the transaction, so nothing about the chain needs to travel out with this result.
     */
    data class Rotated(val tokens: SessionTokens) : RefreshResult

    /** Token is unknown, expired, or deliberately revoked. Nothing was written. */
    data object Rejected : RefreshResult

    /**
     * Reuse detected — the family has been torn down and the commit must stand. [familyId]
     * names the chain, whose cached grace entries have to go with it.
     */
    data class Compromised(val familyId: String) : RefreshResult
}
