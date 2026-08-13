package com.store.core.presentation.ui.base

import com.store.core.presentation.ui.base.actions.InputFieldAction
import com.store.core.utils.Logger
import com.store.core.utils.e
import com.store.core.domain.model.FieldKey

/**
 * Represents a change in the state of an input field, such as its value or a focus lost event.
 *
 * This class is used to encapsulate information about a change of a text field,
 * including its new value, and whether the change was caused by losing focus.
 *
 * @property value The new value of the field as a [String].
 * @property field The specific field being updated, represented by [Field].
 *              Defaults to [Field.Unspecified] if no field is explicitly provided.
 * @property focusLost Indicates whether the change was triggered by the field losing focus.
 *                    Defaults to `false`.
 */
interface InputFieldChanged : InputFieldAction {
    val value: String
    val field: Field
    val focusLost: Boolean

    companion object {
        // Factory method to create an instance of InputFieldChanged
        operator fun invoke(
            value: String,
            field: Field = Field.Unspecified,
            focusLost: Boolean = false
        ): InputFieldChanged = Impl(value, field, focusLost)
    }

    data class Impl(
        override val value: String,
        override val field: Field,
        override val focusLost: Boolean
    ) : InputFieldChanged

}

data class Revalidate(
    override val field: Field,
    override val focusLost: Boolean = true
) : InputFieldChanged {
    override val value: String
        get() {
            Logger.e(
                "InputFieldValidation",
                "You are trying to get value from Revalidate InputFieldChanged. Use previous from view data."
            )
            return "NO_VALUE_USE_PREVIOUS"
        }
}

fun InputFieldChanged.copy(
    value: String = this.value,
    field: Field = this.field,
    focusLost: Boolean = this.focusLost
): InputFieldChanged = InputFieldChanged.Impl(value, field, focusLost)

interface Field {
    /** Identity for per-field job keys — see [com.store.core.presentation.coroutines.SingleJobLaunch]. */
    val jobKey: String get() = this::class.simpleName ?: "Unspecified"

    object Unspecified : Field
}

interface KeyedField : Field {
    val key: FieldKey
}
