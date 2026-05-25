package com.feature.authentication.data.di

import com.feature.authentication.data.DefaultAuthRepository
import com.feature.authentication.data.DefaultCustomerRepository
import com.feature.authentication.data.data_source.AuthDataSource
import com.feature.authentication.data.data_source.CustomerDataSource
import com.feature.authentication.data.data_source.FirebaseLocalAuthSessionDataSource
import com.feature.authentication.domain.model.GoogleSignInError
import com.feature.authentication.domain.model.request.AuthUserRequest
import com.feature.authentication.domain.repository.AuthRepository
import com.feature.authentication.domain.repository.CustomerRepository
import com.feature.authentication.domain.repository.GoogleSignInService
import com.store.core.domain.ApiResult
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage

internal actual class PlatformRepositoryProvider actual constructor(
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
    private val customerDataSource: CustomerDataSource,
    private val authDataSource: AuthDataSource,
    secureStorage: SecureStorage
) {
    actual fun createAuthRepository(): AuthRepository =
        DefaultAuthRepository(
            api = authDataSource,
            localAuthSessionDataSource = localAuthSessionDataSource
        )

    actual fun createCustomerRepository(): CustomerRepository =
        DefaultCustomerRepository(
            api = customerDataSource,
            localAuthSessionDataSource = localAuthSessionDataSource,
        )

    actual fun createGoogleSignInService(): GoogleSignInService =
        object : GoogleSignInService {
            override suspend fun signIn(): ApiResult<AuthUserRequest, GoogleSignInError> =
                ApiResult.Error(GoogleSignInError.DesktopNotSupported())
        }
}

internal actual fun provideAuthSessionDataSource(secureStorage: SecureStorage): LocalAuthSessionDataSource =
    FirebaseLocalAuthSessionDataSource(secureStorage)
