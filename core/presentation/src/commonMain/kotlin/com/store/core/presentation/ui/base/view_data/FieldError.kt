package com.store.core.presentation.ui.base.view_data

import com.store.core.presentation.utils.UiText


data class FieldError(
    val indicateError: Boolean = false,
    val errorMessage: UiText = UiText.Empty
) {

    constructor(error: String) : this(
        indicateError = error.isNotEmpty(),
        errorMessage = if (error.isEmpty()) UiText.Empty else UiText.Raw(error)
    )

    constructor(error: UiText) : this(
        indicateError = error != UiText.Empty,
        errorMessage = error
    )

    constructor(indicateError: Boolean) : this(
        indicateError = indicateError,
        errorMessage = UiText.Empty
    )

    val hasError: Boolean = indicateError || errorMessage != UiText.Empty
    val hasNoError: Boolean = !indicateError && errorMessage == UiText.Empty

    companion object {
        val NO_ERROR = FieldError(
            indicateError = false,
            errorMessage = UiText.Empty
        )

        fun of(errorMessage: UiText?): FieldError {
            return if (errorMessage == null || errorMessage == UiText.Empty) NO_ERROR
            else FieldError(
                indicateError = true,
                errorMessage = errorMessage
            )
        }
    }
}
