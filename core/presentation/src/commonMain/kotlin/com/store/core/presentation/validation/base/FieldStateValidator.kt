package com.store.core.presentation.validation.base

import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.ui.base.InputFieldChanged
import kotlinx.coroutines.flow.StateFlow


/**
 * Interface for validating input fields and producing updated view data.
 *
 * @param T The type of the view data being validated and returned.
 */
fun interface FieldStateValidator<T> {
    fun validate(changed: InputFieldChanged, ctx: ActionHandlerScope<T>)
}

interface BlockingFieldValidator<T> : FieldStateValidator<T> {

    val inProgress: StateFlow<Boolean>

    suspend fun validateBlocking(changed: InputFieldChanged, ctx: ActionHandlerScope<T>): T
}