package com.store.di.modules.auth

import com.feature.authentication.data.RemoteDataSource
import com.feature.authentication.domain.repository.CustomerRepository
import com.feature.authentication.domain.usecases.CreateCustomerUseCase
import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.authentication.domain.usecases.UpdateCustomerUseCase
import com.feature.authentication.presentation.AuthenticationViewModel
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal expect class PlatformRepositoryProvider(remoteDataSource: RemoteDataSource) {
    fun createCustomerRepository(): CustomerRepository
}

private val authRepositoryModule = module {
    single<CustomerRepository> {
        PlatformRepositoryProvider(get()).createCustomerRepository()
    }
}

private val authUseCaseModule = module {
    factoryOf(::CreateCustomerUseCase)
    factoryOf(::GetCurrentUserIdUseCase)
    factoryOf(::ReadCustomerUseCase)
    factoryOf(::UpdateCustomerUseCase)
    factoryOf(::SignOutUseCase)
}

private val authViewModelModule = module {
    viewModel {
        AuthenticationViewModel(
            get(),
            mainDispatcher = get(named<MainDispatcher>()),
            ioDispatcher = get(named<IoDispatcher>()),
        )
    }
}

val authFeatureModule = module {
    includes(
        authRepositoryModule,
        authUseCaseModule,
        authViewModelModule,
    )
}
