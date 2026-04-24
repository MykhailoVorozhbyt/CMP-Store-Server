package org.cmp.store.presentation

import com.feature.authentication.domain.usecases.GetCurrentUserIdUseCase
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.store.core.presentation.core.di.coroutines.IoDispatcher
import com.store.core.presentation.core.di.coroutines.MainDispatcher
import com.store.core.presentation.core.viewmodel.BaseActionHandleViewModel
import com.store.core.presentation.ui.ViewAction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.cmp.store.navigation.Screen

class AppViewModel(
    getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    @MainDispatcher mainDispatcher: CoroutineDispatcher,
    @IoDispatcher ioDispatcher: CoroutineDispatcher,
) : BaseActionHandleViewModel<AppViewData>(mainDispatcher, ioDispatcher) {

    override val _viewData = MutableStateFlow(AppViewData())

    init {
        val startDestination =
            if (getCurrentUserIdUseCase() != null) Screen.HomeGraph() else Screen.Auth
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(serverId = WEB_CLIENT_ID)
        )
        _viewData.update { it.copy(appReady = true, startDestination = startDestination) }
    }

    override suspend fun handleViewAction(action: ViewAction) = Unit

    companion object Constants {
        const val WEB_CLIENT_ID =
            "270317432366-vkjja1fka4tldblvjcslblivetqh0ue7.apps.googleusercontent.com"
    }

}