package com.feature.authentication.domain.usecases

import com.feature.authentication.domain.model.SignInResult
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.feature.authentication.domain.repository.AuthRepository
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse
import com.store.core.domain.ApiResult
import org.cmp.store.network.NetworkError

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
    ): SignInResult = repository.authorize(
        request = AuthRequest(
            provider = AuthProvider.MANUAL,
            email = email,
            password = password
        )
    ).toSignInResult()

    suspend operator fun invoke(
        user: AuthUserRequest?,
    ): SignInResult {
        val email = user?.email ?: return SignInResult.Failure(NetworkError.EMAIL_REQUIRED)
        val providerUserId =
            user.uid ?: return SignInResult.Failure(NetworkError.PROVIDER_USER_ID_REQUIRED)
        return repository.authorize(
            AuthRequest(
                provider = AuthProvider.GOOGLE,
                email = email,
                providerUserId = providerUserId,
                displayName = user.displayName
            )
        ).toSignInResult()
    }

    private fun ApiResult<AuthResponse, NetworkError>.toSignInResult(): SignInResult =
        when (this) {
            is ApiResult.Success -> SignInResult.Success(data.isNewAccount)
            is ApiResult.Error -> SignInResult.Failure(error)
        }
}
