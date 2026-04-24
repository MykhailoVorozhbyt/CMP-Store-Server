package com.store.di

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
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatchersModule: Module = module {
    single<CoroutineDispatcher>(named<IoDispatcher>()) { Dispatchers.IO }
    single<CoroutineDispatcher>(named<DefaultDispatcher>()) { Dispatchers.Default }
    single<CoroutineDispatcher>(named<MainDispatcher>()) { Dispatchers.Main }
    single<CoroutineDispatcher>(named<UnconfinedDispatcher>()) { Dispatchers.Unconfined }
    single<CoroutineScope>(named<ApplicationScope>()) {
        CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>(named<DefaultDispatcher>()))
    }
}
