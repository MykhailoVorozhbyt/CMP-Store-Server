package com.store.core.presentation.validation.password

import com.store.core.domain.model.validation.email.Validator
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.UiText
import com.store.core.presentation.validation.base.BlankStringValidator
import com.store.core.presentation.validation.base.InputFieldValidator
import com.store.core.resources.Res
import com.store.core.resources.common_password_error_max_lenght
import com.store.core.resources.common_password_error_mix_lenght

class PasswordFieldValidator : InputFieldValidator(
    localValidators = setOf(
        BlankStringValidator(),
        MinLengthValidator(MIN_PASSWORD_LENGTH),
        MaxLengthValidator(MAX_PASSWORD_LENGTH)
    )
) {
    override fun localError(
        value: String,
        previous: InputFieldViewData
    ): UiText = when {
        value.isBlank() -> super.localError(value, previous)
        value.length < MIN_PASSWORD_LENGTH ->
            UiText.ResourceArgs(Res.string.common_password_error_mix_lenght, MIN_PASSWORD_LENGTH)

        value.length > MAX_PASSWORD_LENGTH ->
            UiText.ResourceArgs(Res.string.common_password_error_max_lenght, MAX_PASSWORD_LENGTH)

        else -> UiText.Empty
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
        const val MAX_PASSWORD_LENGTH = 16
    }
}

private class MinLengthValidator(
    private val minLength: Int
) : Validator<String> {
    override fun isValid(value: String): Boolean = value.length >= minLength
}

private class MaxLengthValidator(
    private val maxLength: Int
) : Validator<String> {
    override fun isValid(value: String): Boolean = value.length <= maxLength
}
