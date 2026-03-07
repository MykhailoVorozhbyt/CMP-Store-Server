package com.store.core.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.store.core.resources.Res
import com.store.core.resources.app_name
import com.store.core.resources.please_wait
import com.store.core.resources.sign_in_text
import com.store.core.resources.sign_in_with_google

val LocalStoreStrings = staticCompositionLocalOf<AppStrings> {
    error("StoreStrings no provided")
}

interface AppStrings {
    val appName get() = Res.string.app_name
    val signInText get() = Res.string.sign_in_text
    val signInWithGoogle get() = Res.string.sign_in_with_google
    val pleaseWait get() = Res.string.please_wait
}