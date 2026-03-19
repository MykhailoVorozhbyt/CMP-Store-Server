package com.store.core.presentation.core.di.coroutines

import kotlin.annotation.AnnotationRetention.BINARY

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val niaDispatcher: NiaDispatchers)

enum class NiaDispatchers { Default, IO }
