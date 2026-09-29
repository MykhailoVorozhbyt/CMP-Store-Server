package com.store.component.auth.data.di

import com.store.component.auth.data.data_source.AuthDataSource
import com.store.component.auth.data.data_source.KtorAuthDataSource
import com.store.component.auth.domain_api.AuthRepository
import com.store.component.auth.domain_api.GoogleSignInService
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect class PlatformRepositoryProvider(
    localAuthSessionDataSource: LocalAuthSessionDataSource,
    authDataSource: AuthDataSource,
    secureStorage: SecureStorage
) {
    fun createAuthRepository(): AuthRepository
    fun createGoogleSignInService(): GoogleSignInService
}

internal expect fun provideAuthSessionDataSource(secureStorage: SecureStorage): LocalAuthSessionDataSource

val authenticationDataModule = module {
    singleOf(::provideAuthSessionDataSource)
    singleOf(::KtorAuthDataSource).bind(AuthDataSource::class)
    singleOf(::PlatformRepositoryProvider)

    single<AuthRepository> { get<PlatformRepositoryProvider>().createAuthRepository() }
    single<GoogleSignInService> { get<PlatformRepositoryProvider>().createGoogleSignInService() }
}
