package com.store.core.presentation.validation.email

import com.store.core.domain.model.validation.email.EmailDomainValidator
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.UiText
import com.store.core.presentation.validation.base.InputFieldValidator
import com.store.core.resources.Res
import com.store.core.resources.auth_error_incorrect_email_address_format
import com.store.core.resources.auth_validation_domain

class EmailFieldValidator(
    emailValidator: EmailPatternValidator,
    private val emailDomainValidator: EmailDomainValidator,
) : InputFieldValidator(
    localValidators = setOf(emailValidator),
    getLocalError = { UiText.Resource(Res.string.auth_error_incorrect_email_address_format) }
) {

    override fun validate(
        changed: InputFieldChanged,
        previous: InputFieldViewData
    ): InputFieldViewData {
        val base = super.validate(changed, previous)

        if (base.input.isBlank()) {
            return base.copy(warningMessage = UiText.Empty)
        }

        val warn = if (base.isLocalValid) {
            if (emailDomainValidator.isValid(base.input)) UiText.Empty
            else UiText.Resource(Res.string.auth_validation_domain)
        } else {
            base.warningMessage
        }

        return base.copy(warningMessage = warn)
    }
}
