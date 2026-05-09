package com.store.di.modules.auth

import com.feature.authentication.data.JvmCustomerRepositoryImpl
import com.feature.authentication.data.RemoteDataSource
import com.feature.authentication.domain.repository.CustomerRepository

internal actual class PlatformRepositoryProvider actual constructor(remoteDataSource: RemoteDataSource) {
    actual fun createCustomerRepository(): CustomerRepository = JvmCustomerRepositoryImpl()
}