package com.store.component.product.data.di

import com.store.component.product.data.DefaultProductRepository
import com.store.component.product.data.data_source.KtorProductDataSource
import com.store.component.product.data.data_source.ProductDataSource
import com.store.component.product.domain_api.ProductRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val productDataModule = module {
    singleOf(::KtorProductDataSource).bind<ProductDataSource>()
    singleOf(::DefaultProductRepository).bind<ProductRepository>()
}
