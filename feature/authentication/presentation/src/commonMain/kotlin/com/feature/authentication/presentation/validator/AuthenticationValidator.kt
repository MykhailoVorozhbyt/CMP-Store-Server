package com.feature.authentication.presentation.validator

import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.validation.base.SuspendFieldValidator
import com.store.core.presentation.validation.email.EmailFieldValidator
import com.store.core.presentation.validation.password.PasswordFieldValidator

class AuthenticationValidator(
    private val emailValidator: EmailFieldValidator,
    private val passwordValidator: PasswordFieldValidator,
) : SuspendFieldValidator.Abstract<AuthenticationViewData>() {

    override fun validateFast(
        changed: InputFieldChanged,
        previous: AuthenticationViewData
    ): AuthenticationViewData {
        val email = emailValidator.validate(changed, previous.manual.email)
        val password = passwordValidator.validate(changed, previous.manual.password)
        return previous.copy(
            manual = previous.manual.copy(
                email = email,
                password = password,
            )
        )
    }
}
