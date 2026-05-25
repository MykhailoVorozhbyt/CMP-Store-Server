package com.feature.home.presentation

import com.feature.authentication.domain.usecases.SignOutUseCase
import com.feature.home.presentation.utils.opposite
import com.feature.home.presentation.view_data.HomeGraphInitializer
import com.feature.home.presentation.view_data.HomeGraphViewAction
import com.feature.home.presentation.view_data.HomeGraphViewData
import com.store.core.presentation.core.di.coroutines.AppDispatchers
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import com.store.core.resources.Res
import com.store.core.resources.common_error_calculating
import com.store.core.resources.common_error_sign_out
import kotlinx.coroutines.flow.MutableStateFlow
import com.store.core.presentation.navigation.Screen
import com.store.core.domain.onError
import com.store.core.domain.onSuccess
import org.jetbrains.compose.resources.getString

class HomeGraphViewModel(
    initializer: HomeGraphInitializer,
    private val signOutUseCase: SignOutUseCase,
    dispatchers: AppDispatchers
) : BaseActionHandleViewModel<HomeGraphViewData>(dispatchers) {

    override val _viewData = MutableStateFlow(HomeGraphViewData())

    override suspend fun handleViewAction(action: ViewAction) {
        when (action) {
            HomeGraphViewAction.CheckoutClicked -> handleCheckoutClicked()
            HomeGraphViewAction.SignOutClicked -> handleSignOutClicked()
        }
    }

    init {
        initializer.initialize(this)
    }

    private fun handleSignOutClicked() {
        launchIo {
            signOutUseCase()
                .onSuccess { navigateInclusive(Screen.Auth) }
                .onError { showError(getString(Res.string.common_error_sign_out)) }
        }
    }

    private suspend fun handleCheckoutClicked() {
        when {
            viewData.totalAmountFlow.isSuccess() -> {
                navigate(
                    Screen.Checkout(
                        viewData.totalAmountFlow.successData().toString()
                    )
                )
            }

            viewData.totalAmountFlow.isError() -> {
                showError(
                    getString(
                        Res.string.common_error_calculating,
                        viewData.totalAmountFlow.errorMessage()
                    )
                )
            }
        }
    }

    fun onDrawerToggle() {
        updateViewData {
            copy(drawerState = drawerState.opposite())
        }
    }

}
