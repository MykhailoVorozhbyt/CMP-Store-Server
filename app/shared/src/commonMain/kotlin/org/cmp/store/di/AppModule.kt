package org.cmp.store.di

import org.cmp.store.presentation.AppViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appViewModelModule = module {
    viewModelOf(::AppViewModel)
}
