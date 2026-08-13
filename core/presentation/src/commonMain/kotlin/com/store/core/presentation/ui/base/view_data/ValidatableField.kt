package com.store.core.presentation.ui.base.view_data

import androidx.compose.runtime.MutableState
import com.store.core.presentation.ui.base.Field

interface ValidatableField<T> {
    val field: Field
    val value: T
    val valueStr: String
    val localError: FieldError
    val remoteError: FieldError
    val isLocalValid: Boolean

    val error: FieldError get() = if (remoteError.hasError) remoteError else localError
    val isAllValid: Boolean get() = isLocalValid && error.hasNoError
}

interface ValidatableUpdatableField<SELF : ValidatableUpdatableField<SELF, *>, VALUE> :
    ValidatableField<VALUE> {
    fun copyWith(
        localError: FieldError = this.localError,
        remoteError: FieldError = this.remoteError,
        isLocalValid: Boolean = this.isLocalValid && localError.hasNoError,
    ): SELF
}

interface ValidatableStringField : ValidatableField<String> {
    override val valueStr: String get() = value
}

interface InputField {
    val field: Field
    val inputState: MutableState<String>
    var input: String
        get() = inputState.value;
        set(value) {
            inputState.value = value
        }

    val hint: String
    val enabled: Boolean
    val config: InputFieldConfig
}

interface ValidatableInputField : InputField, ValidatableStringField {
    override val value: String get() = input
}

interface ValidatableUpdatableInputField<SELF : ValidatableUpdatableField<SELF, *>>
    : ValidatableInputField, ValidatableUpdatableField<SELF, String> {

    fun reset() = copyWith(
        localError = FieldError.NO_ERROR,
        remoteError = FieldError.NO_ERROR,
        isLocalValid = false,
    ).apply { input = "" }

}
