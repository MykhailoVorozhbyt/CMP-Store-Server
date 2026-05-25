package com.store.core.presentation.utils

fun normalizeQuotes(input: String): String {
    return input
        .replace("[’‘`ʼʻʽ′]".toRegex(), "'")
        .replace("[“”„‟″]".toRegex(), "\"")
}

/**
 * Sanitizes the input string by keeping only characters whose inclusion keeps the whole
 * accumulated prefix matching the [allowed] pattern, and limits the output to [maxLength].
 *
 * For example, simple no-spaces regex will strip all spaces from the input,
 * and [emailRegexRule] keeps its sequence rules (no leading `.`, no `..`, single `@`) on paste.
 *
 * Only works with prefix-closed filtering rules (character classes with `*`/`+`,
 * negative-lookahead substring bans — e.g. [emailRegexRule], [noSpaceRegexRule]).
 * Structural patterns like `^[A-Z]{2}\d{1,3}$` collapse to an empty string,
 * because no single-character prefix can ever match them.
 *
 * @return A `sanitized string` or `an empty string` if no valid characters are found.
 */
fun sanitizeString(raw: String, allowed: Regex?, maxLength: Int): String {
    if (maxLength <= 0) return ""
    if (allowed == null) {
        return if (raw.length <= maxLength) raw else raw.take(maxLength)
    }

    val builder = StringBuilder(minOf(raw.length, maxLength))
    for (ch in raw) {
        if (builder.length >= maxLength) break
        builder.append(ch)
        if (!allowed.matches(builder)) {
            builder.deleteAt(builder.length - 1)
        }
    }

    return builder.toString()
}
