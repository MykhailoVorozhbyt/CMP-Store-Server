package com.store.di.modules.home

import com.feature.home.data.ProductRepositoryImpl
import com.feature.home.domain.repository.ProductRepository
import com.feature.home.domain.usecases.ReadDiscountedProductsUseCase
import com.feature.home.domain.usecases.ReadNewProductsUseCase
import com.feature.home.domain.usecases.ReadProductByIdUseCase
import com.feature.home.domain.usecases.ReadProductsByCategoryUseCase
import com.feature.home.domain.usecases.ReadProductsByIdsUseCase
import com.feature.home.presentation.HomeGraphViewModel
import com.feature.home.presentation.view_data.HomeGraphInitializer
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private val homeRepositoryModule = module {
    singleOf(::ProductRepositoryImpl).bind(ProductRepository::class)
}

private val homeUseCaseModule = module {
    factoryOf(::ReadDiscountedProductsUseCase)
    factoryOf(::ReadNewProductsUseCase)
    factoryOf(::ReadProductByIdUseCase)
    factoryOf(::ReadProductsByCategoryUseCase)
    factoryOf(::ReadProductsByIdsUseCase)
}

private val homePresentationModule = module {
    singleOf(::HomeGraphInitializer)
}

private val homeViewModelModule = module {
    viewModel {
        HomeGraphViewModel(
            get(),
            get(),
            mainDispatcher = get(named<MainDispatcher>()),
            ioDispatcher = get(named<IoDispatcher>()),
        )
    }
}

val homeFeatureModule = module {
    includes(
        homeRepositoryModule,
        homeUseCaseModule,
        homePresentationModule,
        homeViewModelModule,
    )
}
