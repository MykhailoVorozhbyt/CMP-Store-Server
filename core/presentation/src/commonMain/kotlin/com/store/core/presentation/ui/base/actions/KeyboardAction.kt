package com.store.core.presentation.ui.base.actions

import com.store.core.presentation.ui.base.Field

interface KeyboardAction : InputFieldAction {
    val field: Field get() = Field.Unspecified

    interface Done : KeyboardAction {
        data class Impl(override val field: Field) : Done
        companion object {
            operator fun invoke(field: Field = Field.Unspecified) = Impl(field)
        }
    }

    interface Next : KeyboardAction {
        data class Impl(override val field: Field) : Next
        companion object {
            operator fun invoke(field: Field = Field.Unspecified) = Impl(field)
        }
    }
}
