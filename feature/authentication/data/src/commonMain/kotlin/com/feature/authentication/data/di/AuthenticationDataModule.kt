package com.feature.authentication.data.di

import com.feature.authentication.data.data_source.AuthDataSource
import com.feature.authentication.data.data_source.CustomerDataSource
import com.feature.authentication.data.data_source.KtorAuthDataSource
import com.feature.authentication.data.data_source.KtorCustomerDataSource
import com.feature.authentication.domain.repository.AuthRepository
import com.feature.authentication.domain.repository.CustomerRepository
import com.feature.authentication.domain.repository.GoogleSignInService
import com.store.core.security.LocalAuthSessionDataSource
import com.store.core.security.SecureStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect class PlatformRepositoryProvider(
    localAuthSessionDataSource: LocalAuthSessionDataSource,
    customerDataSource: CustomerDataSource,
    authDataSource: AuthDataSource,
    secureStorage: SecureStorage
) {
    fun createAuthRepository(): AuthRepository
    fun createCustomerRepository(): CustomerRepository
    fun createGoogleSignInService(): GoogleSignInService
}

internal expect fun provideAuthSessionDataSource(secureStorage: SecureStorage): LocalAuthSessionDataSource

val authenticationDataModule = module {
    singleOf(::provideAuthSessionDataSource)
    singleOf(::KtorAuthDataSource).bind(AuthDataSource::class)
    singleOf(::KtorCustomerDataSource).bind(CustomerDataSource::class)
    singleOf(::PlatformRepositoryProvider)

    single<AuthRepository> { get<PlatformRepositoryProvider>().createAuthRepository() }
    single<CustomerRepository> { get<PlatformRepositoryProvider>().createCustomerRepository() }
    single<GoogleSignInService> { get<PlatformRepositoryProvider>().createGoogleSignInService() }
}
