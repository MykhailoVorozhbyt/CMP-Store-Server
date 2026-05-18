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
    val signInText get() = Res.string.auth_title
    val signInWithGoogle get() = Res.string.auth_btn_google
    val pleaseWait get() = Res.string.common_loading
    val authenticationSuccessful get() = Res.string.auth_success
    val unknownError get() = Res.string.common_error_unknown
    val internetConnectionUnavailable get() = Res.string.common_error_no_internet
    val signInCanceled get() = Res.string.auth_error_canceled

}