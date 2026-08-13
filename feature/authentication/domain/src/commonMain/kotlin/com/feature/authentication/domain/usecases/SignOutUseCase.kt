package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.repository.AuthRepository
import com.store.core.domain.EmptyResult
import org.cmp.store.network.NetworkError

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): EmptyResult<NetworkError> = repository.signOut()
}
