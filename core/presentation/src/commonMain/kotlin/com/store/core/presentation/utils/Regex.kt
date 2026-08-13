package com.store.core.presentation.utils

val intRegexRules = "[0-9]*".toRegex()

/**
 * This is not full email validation regex, it is only for input filtering
 * test@gmail.com
 */
val emailRegexRule =
    """^(?![.@])(?!.*\.\.)(?!.*\.@)(?!.*@\.)(?!.*@.*@)[A-Za-z0-9!#$%&'*+/=?^_`{|}~.@-]*$""".toRegex()

val noSpaceRegexRule = """^\S*$""".toRegex()