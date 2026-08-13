package com.store.core.presentation.ui.base.actions

import com.store.core.presentation.ui.ViewAction

interface OnCloseBottomSheet : ViewAction {
    val tag: String get() = "NOT PROVIDED"

    data class Tagged(override val tag: String) : OnCloseBottomSheet
}