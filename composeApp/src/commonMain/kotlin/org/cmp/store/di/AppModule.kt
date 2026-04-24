package org.cmp.store.di

import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import org.cmp.store.presentation.AppViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appViewModelModule = module {
    viewModel {
        AppViewModel(
            get(),
            mainDispatcher = get(named<MainDispatcher>()),
            ioDispatcher = get(named<IoDispatcher>())
        )
    }
}
