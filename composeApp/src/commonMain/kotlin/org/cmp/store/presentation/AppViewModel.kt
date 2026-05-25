package org.cmp.store.presentation

import com.feature.authentication.domain.model.WEB_CLIENT_ID
import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.store.core.presentation.core.di.coroutines.AppDispatchers
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.cmp.store.getPlatform
import com.store.core.presentation.navigation.Screen

class AppViewModel(
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    dispatchers: AppDispatchers
) : BaseActionHandleViewModel<AppViewData>(dispatchers) {

    override val _viewData = MutableStateFlow(AppViewData())

    init {
        launchIo {
            if (getPlatform().isMobile) {
                createGoogleAuthProvider()
            }
            val startDestination =
                if (getCurrentUserIdUseCase() != null) Screen.HomeGraph() else Screen.Auth
            _viewData.update { it.copy(appReady = true, startDestination = startDestination) }
        }
    }

    override suspend fun handleViewAction(action: ViewAction) = Unit

    fun createGoogleAuthProvider() {
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = WEB_CLIENT_ID))
    }
}
