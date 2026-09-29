package com.store.component.auth.usecase

import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.model.AuthError
import com.store.core.domain.EmptyResult

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): EmptyResult<AuthError> = repository.signOut()
}
