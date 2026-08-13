package com.store.core.presentation.validation.base

import com.store.core.domain.model.validation.email.Validator
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.Revalidate
import com.store.core.presentation.ui.base.view_data.FieldError
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.ui.base.view_data.ValidatableUpdatableInputField
import com.store.core.presentation.utils.UiText
import com.store.core.resources.Res
import com.store.core.resources.common_empty
import com.store.core.resources.common_required_field

open class InputFieldValidator(
    localValidators: Set<Validator<String>> = setOf(BlankStringValidator()),
    getLocalError: (() -> UiText)? = null,
) : AbstractInputFieldValidator<InputFieldViewData>(
    localValidators,
    getLocalError
)

abstract class AbstractInputFieldValidator<T : ValidatableUpdatableInputField<T>>(
    private val localValidators: Set<Validator<String>> = setOf(BlankStringValidator()),
    private val getLocalError: (() -> UiText)? = null,
) : FieldValidator<T> {

    private var lastInput = ""

    override fun validate(
        changed: InputFieldChanged,
        previous: T
    ): T {
        if (!shouldStartValidation(changed, previous)) return previous

        val value = if (changed is Revalidate) previous.input else changed.value
        val isLocalValid = isValid(value, previous)
        val localError = when {
            isLocalValid -> FieldError.NO_ERROR
            value.isEmpty() -> FieldError.NO_ERROR
            changed.focusLost -> FieldError(true, localError(value, previous))
            else -> previous.error
        }

        val inputChanged = lastInput != value
        val remoteError = when {
            value.isEmpty() -> FieldError.NO_ERROR
            isLocalValid && inputChanged -> FieldError.NO_ERROR
            else -> previous.remoteError
        }
        lastInput = value
        return previous.copyWith(
            localError = localError,
            remoteError = remoteError,
            isLocalValid = isLocalValid,
        ).apply { input = value }
    }

    protected open fun shouldStartValidation(changed: InputFieldChanged, previous: T): Boolean =
        changed.field == previous.field

    protected open fun isValid(value: String, previous: T): Boolean {
        return localValidators.all { it.isValid(value) }
    }

    protected open fun localError(
        value: String,
        previous: T
    ): UiText {
        return when {
            getLocalError != null -> getLocalError()
            value.isBlank() -> UiText.Resource(Res.string.common_required_field)
            else -> UiText.Resource(Res.string.common_empty)
        }
    }
}

class BlankStringValidator : Validator<String> {
    override fun isValid(value: String): Boolean = value.isNotBlank()
}
