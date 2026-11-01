package com.store.feature.profile.validator

import com.store.core.domain.model.validation.email.Validator
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.UiText
import com.store.core.presentation.validation.base.BlankStringValidator
import com.store.core.presentation.validation.base.InputFieldValidator
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.profile_field_length

class LengthRangeFieldValidator(
    private val range: IntRange,
) : InputFieldValidator(
    localValidators = setOf(BlankStringValidator(), LengthRangeValidator(range)),
) {
    override fun localError(value: String, previous: InputFieldViewData): UiText = when {
        value.isBlank() -> super.localError(value, previous)
        else -> UiText.ResourceArgs(Res.string.profile_field_length, range.first, range.last)
    }
}

private class LengthRangeValidator(private val range: IntRange) : Validator<String> {
    override fun isValid(value: String): Boolean = value.trim().length in range
}
