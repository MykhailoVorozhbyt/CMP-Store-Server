package com.store.core.domain.model.validation.email

open class DefaultStringValidator(
    private val minLength: Int = 0,
    private val maxLength: Int = Int.MAX_VALUE,
    private val regex: Regex? = null,
    private val allowBlank: Boolean = false,
): Validator<String> {

    @Suppress("ReturnCount")
    override fun isValid(value: String): Boolean {
        if (allowBlank.not() and value.isBlank()) return false
        if (value.length !in minLength..maxLength) return false
        if (regex != null && !value.matches(regex)) return false
        return true
    }
}