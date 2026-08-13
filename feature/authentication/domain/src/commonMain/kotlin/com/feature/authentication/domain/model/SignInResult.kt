package com.feature.authentication.domain.model

import org.cmp.store.network.NetworkError

sealed interface SignInResult {
    data class Success(val isNewReg: Boolean) : SignInResult
    data class Failure(val error: NetworkError) : SignInResult
}
