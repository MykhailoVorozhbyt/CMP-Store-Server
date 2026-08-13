package com.store.core.presentation.ui.base.actions

import com.store.core.presentation.ui.ViewAction

interface OnSystemBackClick : ViewAction {
    companion object: OnSystemBackClick {
        operator fun invoke() = this
    }
}