package com.store.di

import com.feature.authentication.data.RemoteDataSource
import org.cmp.store.network.createHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    singleOf(::createHttpClient)
    singleOf(::RemoteDataSource)
}