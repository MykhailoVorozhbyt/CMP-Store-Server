package com.store.core.presentation.core.di.coroutines


@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
annotation class UnconfinedDispatcher

@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope
