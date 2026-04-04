package com.store.di

import com.feature.authentication.data.CustomerApi
import com.feature.authentication.data.CustomerRepositoryImpl
import com.feature.authentication.domain.repository.CustomerRepository
import com.feature.authentication.domain.usecases.CreateCustomerUseCase
import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.feature.authentication.domain.usecases.ReadCustomerUseCase
import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.authentication.domain.usecases.UpdateCustomerUseCase
import org.cmp.store.network.createHttpClient
import com.feature.authentication.presentation.AuthenticationViewModel
import com.store.core.presentation.core.di.coroutines.ApplicationScope
import com.store.core.presentation.core.di.coroutines.DefaultDispatcher
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import com.store.core.presentation.core.di.coroutines.UnconfinedDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    singleOf(::createHttpClient)
    singleOf(::CustomerApi)
}

val repositoryModule = module {
    single<CustomerRepository> { CustomerRepositoryImpl(get()) }
}

val useCaseModule = module {
    singleOf(::CreateCustomerUseCase)
    singleOf(::GetCurrentUserIdUseCase)
    singleOf(::ReadCustomerUseCase)
    singleOf(::UpdateCustomerUseCase)
    singleOf(::SignOutUseCase)
}

val viewModelModule = module {
    viewModel {
        AuthenticationViewModel(
            get(),
            mainDispatcher = get(named<MainDispatcher>()),
            ioDispatcher = get(named<IoDispatcher>())
        )
    }
}

val dispatchersModule: Module = module {
    single<CoroutineDispatcher>(named<IoDispatcher>()) { Dispatchers.IO }
    single<CoroutineDispatcher>(named<DefaultDispatcher>()) { Dispatchers.Default }
    single<CoroutineDispatcher>(named<MainDispatcher>()) { Dispatchers.Main }
    single<CoroutineDispatcher>(named<UnconfinedDispatcher>()) { Dispatchers.Unconfined }
    single<CoroutineScope>(named<ApplicationScope>()) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named<DefaultDispatcher>()))
    }
}

expect val targetModule: Module

fun initializeKoin(
    config: (KoinApplication.() -> Unit)? = null,
    vararg appModules: Module
) {
    startKoin {
        config?.invoke(this)
        modules(
            targetModule,
            networkModule,
            repositoryModule,
            useCaseModule,
            dispatchersModule,
            viewModelModule,
            *appModules
        )
    }
}