package org.cmp.store.features.session.models

enum class RefreshOutcome {
    /** Token is good — mint a new pair and spend this one. */
    ROTATE,

    /** Token is unusable (expired). Reject without touching the rest of the chain. */
    REJECT,

    /** Token was already spent — two parties hold it. Drop the whole login chain. */
    REVOKE_FAMILY,
}
