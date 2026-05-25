package com.store.core.domain.model.validation.email

/**
 * Synchronous, side-effect-free validation contract.
 *
 * Implementations return `true` when the supplied value satisfies a single rule.
 * Use this for light, in-memory checks that never suspend.
 */
fun interface Validator<T> {
    fun isValid(value: T): Boolean
}