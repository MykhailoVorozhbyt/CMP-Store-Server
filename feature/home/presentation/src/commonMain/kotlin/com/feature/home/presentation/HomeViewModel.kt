package com.feature.home.presentation

import com.feature.home.presentation.view_data.HomeViewData
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
    @MainDispatcher mainDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : BaseActionHandleViewModel<HomeViewData>(mainDispatcher, ioDispatcher) {
    override val _viewData = MutableStateFlow(HomeViewData())

    override suspend fun handleViewAction(action: ViewAction) {
        // implement after HomeViewData fields are defined
    }
}