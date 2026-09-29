package com.store.component.auth.data.di

import com.store.component.auth.data.DefaultAuthRepository
import com.store.component.auth.data.JvmGoogleSignInService
import com.store.component.auth.data.data_source.AuthDataSource
import com.store.component.auth.data.data_source.DefaultLocalAuthSessionDataSource
import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.domain_api.GoogleSignInService
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout

internal actual class PlatformRepositoryProvider actual constructor(
    private val localAuthSessionDataSource: LocalAuthSessionDataSource,
    private val authDataSource: AuthDataSource,
    secureStorage: SecureStorage
) {
    private val clientSecret = DesktopClientSecretResolver()

    actual fun createAuthRepository(): AuthRepository =
        DefaultAuthRepository(
            api = authDataSource,
            localAuthSessionDataSource = localAuthSessionDataSource
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
