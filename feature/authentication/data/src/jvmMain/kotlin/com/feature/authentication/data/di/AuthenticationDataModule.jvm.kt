package com.feature.authentication.data.di

import com.feature.authentication.data.DefaultAuthRepository
import com.feature.authentication.data.DefaultCustomerRepository
import com.feature.authentication.data.JvmGoogleSignInService
import com.feature.authentication.data.data_source.AuthDataSource
import com.feature.authentication.data.data_source.CustomerDataSource
import com.feature.authentication.data.data_source.DefaultLocalAuthSessionDataSource
import com.feature.authentication.domain.repository.AuthRepository
import com.feature.authentication.domain.repository.CustomerRepository
import com.feature.authentication.domain.repository.GoogleSignInService
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout

internal actual class PlatformRepositoryProvider actual constructor(
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
    private val customerDataSource: CustomerDataSource,
    private val authDataSource: AuthDataSource,
    secureStorage: SecureStorage
) {
    private val clientSecret = DesktopClientSecretResolver()

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
        JvmGoogleSignInService(
            httpClient = createJvmGoogleOAuthHttpClient(),
            clientSecret = clientSecret.resolveDesktopClientSecret(),
        )
}

internal actual fun provideAuthSessionDataSource(secureStorage: SecureStorage): LocalAuthSessionDataSource =
    DefaultLocalAuthSessionDataSource(secureStorage)

private fun createJvmGoogleOAuthHttpClient(): HttpClient = HttpClient {
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 30_000
    }
}
