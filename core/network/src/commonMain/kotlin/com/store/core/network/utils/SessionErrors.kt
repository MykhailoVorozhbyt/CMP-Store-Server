package com.store.core.network.utils

import org.cmp.store.network.NetworkError

/**
 * Whether a failed refresh means the session is finished, rather than merely unavailable now.
 *
 * Dropping a session is irreversible for the user, so it is reserved for errors the server will
 * keep returning. A 429 or a 503 is over in a minute; treating either as terminal would throw
 * away a valid 7-day refresh token because the server was briefly busy.
 */
val NetworkError.isSessionTerminal: Boolean
    get() = when (this) {
        NetworkError.INVALID_REFRESH_TOKEN,
        NetworkError.TOKEN_REUSE_DETECTED,
        // Retrying the same token cannot change a bare 401 either.
        NetworkError.UNAUTHORIZED,
            -> true

        else -> false
    }
