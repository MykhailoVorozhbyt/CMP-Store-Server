package org.cmp.store

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.store.core.navigation.SetupNavGraph
import com.store.core.utils.AdaptivePreview

@AdaptivePreview
@Composable
fun App() {
//        val customerRepository = koinInject<CustomerRepository>()
    var appReady by remember { mutableStateOf(false) }
//        val isUserAuthenticated = remember { customerRepository.getCurrentUserId() != null }
//        val startDestination = remember {
//            if (isUserAuthenticated) Screen.HomeGraph
//            else Screen.Auth
//        }

    LaunchedEffect(Unit) {
        GoogleAuthProvider.create(
            credentials = GoogleAuthCredentials(serverId = Constants.WEB_CLIENT_ID)
        )
        appReady = true
    }

    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = appReady
    ) {
        SetupNavGraph()
    }
}

object Constants {
    const val WEB_CLIENT_ID = "270317432366-vkjja1fka4tldblvjcslblivetqh0ue7.apps.googleusercontent.com"
}
