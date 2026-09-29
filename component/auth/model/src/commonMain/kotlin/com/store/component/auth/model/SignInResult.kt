package com.store.component.auth.model

sealed interface SignInResult {
    data class Success(val isNewReg: Boolean) : SignInResult
    data class Failure(val error: AuthError) : SignInResult
}
