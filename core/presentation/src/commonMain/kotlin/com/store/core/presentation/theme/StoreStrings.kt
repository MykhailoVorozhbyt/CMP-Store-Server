package com.store.core.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.store.core.resources.Res
import com.store.core.resources.*

val LocalStoreStrings = staticCompositionLocalOf<AppStrings> {
    error("StoreStrings no provided")
}

interface AppStrings {
    val appName get() = Res.string.app_name
    //splash screen
    val signInText get() = Res.string.sign_in_text
    val signInWithGoogle get() = Res.string.sign_in_with_google
    val pleaseWait get() = Res.string.please_wait
    val authenticationSuccessful get() = Res.string.authentication_successful
    val unknownError get() = Res.string.unknown_error
    val internetConnectionUnavailable get() = Res.string.internet_connection_unavailable
    val signInCanceled get() = Res.string.sign_in_canceled

}