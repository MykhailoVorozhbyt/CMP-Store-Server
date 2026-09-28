package com.store.component.auth.data.di

import com.store.component.auth.data.DefaultAuthRepository
import com.store.component.auth.data.data_source.AuthDataSource
import com.store.component.auth.data.data_source.FirebaseLocalAuthSessionDataSource
import com.store.component.auth.model.GoogleSignInError
import com.store.component.auth.model.request.AuthUserRequest
import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.domain_api.GoogleSignInService
import com.store.core.domain.ApiResult
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage

internal actual class PlatformRepositoryProvider actual constructor(
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
    private val authDataSource: AuthDataSource,
    secureStorage: SecureStorage
) {
    actual fun createAuthRepository(): AuthRepository =
        DefaultAuthRepository(
            api = authDataSource,
            localAuthSessionDataSource = localAuthSessionDataSource
        )

    actual fun createGoogleSignInService(): GoogleSignInService =
        object : GoogleSignInService {
            override suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError> =
                ApiResult.Error(GoogleSignInError.DesktopNotSupported())
        }
}

internal actual fun provideAuthSessionDataSource(secureStorage: SecureStorage): LocalAuthSessionDataSource =
    FirebaseLocalAuthSessionDataSource(secureStorage)
