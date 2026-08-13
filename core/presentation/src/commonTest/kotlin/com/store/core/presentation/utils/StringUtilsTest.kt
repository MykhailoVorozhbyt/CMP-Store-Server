package com.store.core.presentation.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class StringUtilsTest {

    @Test
    fun `valid email paste is preserved unchanged`() {
        assertEquals("john@gmail.com", sanitizeString("john@gmail.com", emailRegexRule, 64))
    }

    @Test
    fun `email sequence rules survive paste`() {
        assertEquals("john.doe@x.com", sanitizeString("john..doe@x.com", emailRegexRule, 64))
        assertEquals("a@b.com", sanitizeString("a@@b.com", emailRegexRule, 64))
        assertEquals("john@x.com", sanitizeString(".john@x.com", emailRegexRule, 64))
    }

    @Test
    fun `whitespace is stripped on paste`() {
        assertEquals("john@gmail.com", sanitizeString("  john@gmail.com  ", emailRegexRule, 64))
        assertEquals("abc", sanitizeString(" a b c ", noSpaceRegexRule, 64))
    }

    @Test
    fun `character class regex keeps only allowed characters`() {
        val alnumRegex = Regex("[A-Z0-9]+")
        assertEquals("AA12345", sanitizeString("  AA-123-45  ", alnumRegex, 100))
    }

    @Test
    fun `maxLength is enforced`() {
        val digitsRegex = Regex("""\d+""")
        assertEquals("123", sanitizeString("a1b2c3d4e5", digitsRegex, 3))
        assertEquals("", sanitizeString("12345", digitsRegex, 0))
    }

    @Test
    fun `null regex passes input through up to maxLength`() {
        assertEquals("hello", sanitizeString("hello world", null, 5))
        assertEquals("hi", sanitizeString("hi", null, 64))
    }

    @Test
    fun `returns empty string when nothing matches`() {
        assertEquals("", sanitizeString("abc", Regex("""\d+"""), 10))
    }

    @Test
    fun `structural anchored patterns are not supported and collapse to empty`() {
        // No single-character prefix can match a fixed-shape pattern, so every char is dropped.
        assertEquals("", sanitizeString("UA123", Regex("""^[A-Z]{2}\d{1,3}$"""), 100))
    }

    @Test
    fun `Simple character-class regex`() {
        val alnumRegex = Regex("[A-Z0-9]+")
        assertEquals("AA12345", sanitizeString("  AA-123-45  ", alnumRegex, 100))
    }

    @Test
    fun `Enforce maxLength while preserving regex validity`() {
        val digitsRegex = Regex("\\d+")
        assertEquals("123", sanitizeString("a1b2c3d4e5", digitsRegex, 3))
    }

    @Test
    fun `No possible match - empty string`() {
        val digitsRegex = Regex("\\d+")
        assertEquals("", sanitizeString("abc", digitsRegex, 10))
    }

}
