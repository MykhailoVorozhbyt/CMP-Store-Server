package com.store.di

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
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
}

val viewModelModule = module {
    viewModel {
        AuthenticationViewModel(
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
            repositoryModule,
            dispatchersModule,
            viewModelModule,
            *appModules
        )
    }
}