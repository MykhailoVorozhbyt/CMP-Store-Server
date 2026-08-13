package com.store.core.presentation.validation.base

import com.store.core.presentation.coroutines.DebouncedSingleJobLaunch
import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.ui.base.Field
import com.store.core.presentation.ui.base.InputFieldChanged
import com.store.core.presentation.ui.base.Revalidate

/**
 * Abstract class for validating fields with debounce behavior, mimicking a text field focus lost.
 *
 * @param T The type of the view data being validated.
 */
abstract class FieldValidatorWithDebounce<T> : DebouncedSingleJobLaunch(), FieldValidator<T> {

    /**
     * Performs immediate and debounced validation of a field, updating [ActionHandlerScope] directly.
     *
     * Applies instant validation to the changed field and updates the scope's view data.
     * Schedules a debounced validation that simulates a focus lost event and applies the result after [delay] ms.
     */
    fun validateAdvanced(
        fieldChanged: InputFieldChanged,
        ctx: ActionHandlerScope<T>,
        delay: Long = MEDIUM_DEBOUNCE,
    ) {
        validate(fieldChanged, ctx)
        if (fieldChanged.focusLost.not()) {
            ctx.scope.launchDebounced(
                debounce = delayForField(fieldChanged.field, delay),
                jobKey = fieldChanged.field.jobKey,
            ) {
                validate(Revalidate(fieldChanged.field), ctx)
            }
        }
    }

    protected open fun delayForField(field: Field, external: Long): Long = external

    companion object {
        const val MEDIUM_DEBOUNCE = 1500L
    }

}