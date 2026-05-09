package com.store.di.modules.auth

import com.feature.authentication.data.CustomerRepositoryImpl
import com.feature.authentication.data.RemoteDataSource
import com.feature.authentication.domain.repository.CustomerRepository

internal actual class PlatformRepositoryProvider actual constructor(private val remoteDataSource: RemoteDataSource) {
    actual fun createCustomerRepository(): CustomerRepository =
        CustomerRepositoryImpl(remoteDataSource)
}
