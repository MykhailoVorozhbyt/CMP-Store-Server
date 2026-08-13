package com.store.core.presentation.ui.base.view_data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import com.store.core.presentation.ui.base.Field
import com.store.core.presentation.utils.UiText
import kotlin.jvm.JvmOverloads

@Stable
data class InputFieldViewData(
    override val inputState: MutableState<String>,
    override val field: Field = Field.Unspecified,
    override val hint: String = "",
    override val localError: FieldError = FieldError.NO_ERROR,
    override val remoteError: FieldError = FieldError.NO_ERROR,
    override val isLocalValid: Boolean = false,
    override val config: InputFieldConfig = InputFieldConfig.DEFAULT,
    override val enabled: Boolean = true,
    val warningMessage: UiText = UiText.Empty
) : ValidatableUpdatableInputField<InputFieldViewData> {

    @JvmOverloads
    constructor(
        input: String = "",
        hint: String = "",
        field: Field = Field.Unspecified,
        localError: FieldError = FieldError.NO_ERROR,
        remoteError: FieldError = FieldError.NO_ERROR,
        isLocalValid: Boolean = false,
        config: InputFieldConfig = InputFieldConfig.DEFAULT,
        enabled: Boolean = true,
        warningMessage: UiText = UiText.Empty
    ) : this(
        inputState = mutableStateOf(input),
        hint = hint,
        field = field,
        localError = localError,
        remoteError = remoteError,
        isLocalValid = isLocalValid,
        config = config,
        enabled = enabled,
        warningMessage = warningMessage
    )

    override fun copyWith(
        localError: FieldError,
        remoteError: FieldError,
        isLocalValid: Boolean
    ): InputFieldViewData {
        return copy(
            localError = localError,
            remoteError = remoteError,
            isLocalValid = isLocalValid
        )
    }
}

@Immutable
interface InputFieldConfig {
    val maxLength: Int
    val regex: Regex?
    val controlByValidator: Boolean

    companion object {
        operator fun invoke(
            maxLength: Int = Int.MAX_VALUE,
            regex: Regex? = null,
            controlByValidator: Boolean = false
        ) = Impl(
            maxLength = maxLength,
            regex = regex,
            controlByValidator = controlByValidator
        )

        val DEFAULT = object : InputFieldConfig {
            override val maxLength: Int = Int.MAX_VALUE
            override val regex: Regex? = null
            override val controlByValidator: Boolean = false
        }
    }

    data class Impl(
        override val maxLength: Int = Int.MAX_VALUE,
        override val regex: Regex? = null,
        override val controlByValidator: Boolean = false
    ) : InputFieldConfig
}
