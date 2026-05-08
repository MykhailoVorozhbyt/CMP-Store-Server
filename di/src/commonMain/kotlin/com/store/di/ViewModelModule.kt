package com.store.di

import com.feature.authentication.presentation.AuthenticationViewModel
import com.feature.home.presentation.view_data.HomeGraphInitializer
import com.feature.home.presentation.HomeGraphViewModel
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        AuthenticationViewModel(
            get(),
            mainDispatcher = get(named<MainDispatcher>()),
            ioDispatcher = get(named<IoDispatcher>())
        )
    }
    singleOf(::HomeGraphInitializer)
    viewModel {
        HomeGraphViewModel(
            get(), get(),
            mainDispatcher = get(named<MainDispatcher>()),
            ioDispatcher = get(named<IoDispatcher>())
        )
    }
}
