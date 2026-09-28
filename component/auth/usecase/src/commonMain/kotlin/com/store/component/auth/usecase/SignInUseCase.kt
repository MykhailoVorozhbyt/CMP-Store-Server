package com.store.component.auth.usecase

import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.model.AuthError
import com.store.component.auth.model.SignInResult
import com.store.component.auth.model.request.AuthUserRequest
import com.store.core.domain.ApiResult
import org.cmp.store.domain.auth.AuthProvider
import org.cmp.store.domain.auth.request.AuthRequest
import org.cmp.store.domain.auth.response.AuthResponse

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
        val email = user?.email ?: return SignInResult.Failure(AuthError.EmailRequired)
        val providerUserId = user.uid ?: return SignInResult.Failure(AuthError.ProviderUserIdRequired)
        return repository.authorize(
            AuthRequest(
                provider = AuthProvider.GOOGLE,
                email = email,
                providerUserId = providerUserId,
                displayName = user.displayName
            )
        ).toSignInResult()
    }

    private fun ApiResult<AuthResponse, AuthError>.toSignInResult(): SignInResult =
        when (this) {
            is ApiResult.Success -> SignInResult.Success(data.isNewAccount)
            is ApiResult.Error -> SignInResult.Failure(error)
        }
}
