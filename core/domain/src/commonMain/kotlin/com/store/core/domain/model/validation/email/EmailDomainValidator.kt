package com.store.core.domain.model.validation.email

import kotlin.math.abs
import kotlin.math.min

interface EmailDomainValidationConfig {
    val domains: List<String>
    val maxDistance: Int

    class Impl : EmailDomainValidationConfig {
        override val domains: List<String> = listOf()
        override val maxDistance: Int = 16
    }
}

class EmailDomainValidator(private val config: EmailDomainValidationConfig) {

    fun isValid(value: String): Boolean {
        return isPopularDomainLikelyCorrect(value)
    }

    private fun isPopularDomainLikelyCorrect(email: String): Boolean {
        val at = email.indexOf('@')
        if (at <= 0 || at == email.lastIndex) return true
        val domain = email.substring(at + 1).lowercase()

        if (domain in config.domains) return true

        return config.domains.none { provider ->
            abs(domain.length - provider.length) <= config.maxDistance &&
                    (isOneTransposition(domain, provider) ||
                            levenshteinAtMost(domain, provider))
        }
    }

    private fun isOneTransposition(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var i = 0
        while (i < a.length && a[i] == b[i]) i++
        if (i >= a.length - 1) return false
        if (a[i] == b[i + 1] && a[i + 1] == b[i]) {
            for (k in i + 2 until a.length) if (a[k] != b[k]) return false
            return true
        }
        return false
    }

    private fun levenshteinAtMost(a: String, b: String): Boolean {
        val n = a.length
        val m = b.length
        if (abs(n - m) > 2) return false
        if (n == 0) return m <= 2
        if (m == 0) return n <= 2

        if (n > m) return levenshteinAtMost(b, a)

        val maxK = config.maxDistance
        val prev = IntArray(n + 1) { it }
        val curr = IntArray(n + 1)

        for (j in 1..m) {
            curr[0] = j
            val iStart = maxOf(1, j - maxK)
            val iEnd = min(n, j + maxK)

            for (i in 1 until iStart) curr[i] = maxK + 1

            var rowMin = curr[0]
            for (i in iStart..iEnd) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                val del = prev[i] + 1
                val ins = curr[i - 1] + 1
                val sub = prev[i - 1] + cost
                val v = min(min(del, ins), sub)
                curr[i] = v
                if (v < rowMin) rowMin = v
            }
            for (i in iEnd + 1..n) curr[i] = maxK + 1

            if (rowMin > maxK) return false

            for (i in 0..n) prev[i] = curr[i]
        }
        return prev[n] <= maxK
    }
}