package com.store.core.presentation.validation.base

import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.ui.base.InputFieldChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for validating input fields and producing updated view data.
 *
 * @param T The type of the view data being validated and returned.
 */
interface FieldValidator<T> : FieldStateValidator<T>, BlockingFieldValidator<T> {

    /**
     * Validates a field based on the provided value change and previous view data.
     *
     * This method is responsible for validating a field when its value changes and
     * returning the updated view data. Implementations can use the current field value,
     * previous view data, and other business rules to determine the new state.
     *
     * @param changed The event representing the field value change.
     * @param previous The previous state of the view data.
     * @return The updated view data after validation.
     */
    fun validate(changed: InputFieldChanged, previous: T): T


    override fun validate(
        changed: InputFieldChanged,
        ctx: ActionHandlerScope<T>
    ) {
        ctx.updateViewData { validate(changed, ctx.viewData) }
    }

    // Local validators assumed to be instant, so no need for progress state
    override val inProgress: StateFlow<Boolean>
        get() = inProgressForInstantValidator

    override suspend fun validateBlocking(
        changed: InputFieldChanged,
        ctx: ActionHandlerScope<T>
    ): T = validate(changed, ctx.viewData)

}

private val inProgressForInstantValidator = MutableStateFlow(false)
