package com.store.core.presentation.validation.base

import com.store.core.presentation.ui.base.ActionHandlerScope
import com.store.core.presentation.ui.base.InputFieldChanged

/**
 * Validator contract for cases where the caller still uses a suspend API,
 * but the validation itself is fully local.
 */
interface SuspendFieldValidator<T> : BlockingFieldValidator<T> {

    fun validateFast(changed: InputFieldChanged, previous: T): T

    abstract class Abstract<T> : FieldValidatorWithDebounce<T>(), SuspendFieldValidator<T> {

        override fun validate(
            changed: InputFieldChanged,
            ctx: ActionHandlerScope<T>
        ) {
            ctx.updateViewData { validateFast(changed, ctx.viewData) }
        }

        override suspend fun validateBlocking(
            changed: InputFieldChanged,
            ctx: ActionHandlerScope<T>
        ): T = validateFast(changed, ctx.viewData)

        override fun validate(changed: InputFieldChanged, previous: T): T =
            validateFast(changed, previous)
    }
}
