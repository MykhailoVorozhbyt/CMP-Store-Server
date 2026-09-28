package com.store.component.auth.usecase

import com.store.component.auth.domain_api.AuthRepository

class GetCurrentUserIdUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): String? = repository.currentUserId()
}
