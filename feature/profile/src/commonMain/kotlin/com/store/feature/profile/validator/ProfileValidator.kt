package com.store.feature.profile.validator

import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.Revalidate
import com.store.core.presentation.ui.base.view_data.FieldError
import com.store.core.presentation.ui.base.view_data.InputFieldViewData
import com.store.core.presentation.utils.UiText
import com.store.core.presentation.validation.base.SuspendFieldValidator
import com.store.core.resources.Res
import com.store.core.resources.common_required_field
import com.store.feature.profile.view_data.ProfileFields
import com.store.feature.profile.view_data.ProfileFormViewData
import com.store.feature.profile.view_data.ProfileViewData

class ProfileValidator : SuspendFieldValidator.Abstract<ProfileViewData>() {

    private val firstName = LengthRangeFieldValidator(NAME_LENGTH)
    private val lastName = LengthRangeFieldValidator(NAME_LENGTH)
    private val city = LengthRangeFieldValidator(NAME_LENGTH)
    private val postalCode = LengthRangeFieldValidator(POSTAL_CODE_LENGTH)
    private val address = LengthRangeFieldValidator(NAME_LENGTH)
    private val phoneNumber = LengthRangeFieldValidator(PHONE_NUMBER_LENGTH)

    override fun validateFast(changed: InputFieldChanged, previous: ProfileViewData): ProfileViewData =
        previous.copy(form = validateForm(changed, previous.form))

    fun validateAll(form: ProfileFormViewData): ProfileFormViewData =
        VALIDATED_FIELDS.fold(form) { acc, field -> validateForm(Revalidate(field), acc) }

    fun validateForSubmit(form: ProfileFormViewData): ProfileFormViewData {
        val validated = validateAll(form)
        return validated.copy(
            firstName = validated.firstName.requireFilled(),
            lastName = validated.lastName.requireFilled(),
            city = validated.city.requireFilled(),
            postalCode = validated.postalCode.requireFilled(),
            address = validated.address.requireFilled(),
            phone = validated.phone.copy(number = validated.phone.number.requireFilled()),
        )
    }

    private fun InputFieldViewData.requireFilled(): InputFieldViewData =
        if (input.isEmpty()) {
            copyWith(localError = FieldError(UiText.Resource(Res.string.common_required_field)), isLocalValid = false)
        } else {
            this
        }

    private fun validateForm(changed: InputFieldChanged, form: ProfileFormViewData) = form.copy(
        firstName = firstName.validate(changed, form.firstName),
        lastName = lastName.validate(changed, form.lastName),
        city = city.validate(changed, form.city),
        postalCode = postalCode.validate(changed, form.postalCode),
        address = address.validate(changed, form.address),
        phone = form.phone.copy(number = phoneNumber.validate(changed, form.phone.number)),
    )

    private companion object {
        val NAME_LENGTH = 3..50
        val POSTAL_CODE_LENGTH = 3..8
        val PHONE_NUMBER_LENGTH = 5..30
        val VALIDATED_FIELDS = listOf(
            ProfileFields.FirstName,
            ProfileFields.LastName,
            ProfileFields.City,
            ProfileFields.PostalCode,
            ProfileFields.Address,
            ProfileFields.PhoneNumber,
        )
    }
}
