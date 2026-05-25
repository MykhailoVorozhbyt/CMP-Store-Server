package com.feature.home.data.di

import com.feature.home.data.data_source.KtorProductDataSource
import com.feature.home.domain.data_source.ProductDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeDataModule = module {
    singleOf(::KtorProductDataSource).bind(ProductDataSource::class)
}
