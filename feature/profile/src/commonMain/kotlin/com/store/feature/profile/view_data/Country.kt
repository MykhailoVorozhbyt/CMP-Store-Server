package com.store.feature.profile.view_data

import com.store.core.resources.Resources
import com.store.feature.profile.resources.Res
import com.store.feature.profile.resources.country_ukraine
import com.store.feature.profile.resources.country_usa
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Country(
    val dialCode: Int,
    val title: StringResource,
    val flag: DrawableResource,
) {
    Usa(dialCode = 1, title = Res.string.country_usa, flag = Resources.Flag.Usa),
    Ukraine(dialCode = 380, title = Res.string.country_ukraine, flag = Resources.Flag.Ukraine);

    companion object {
        val Default = Ukraine

        fun fromDialCode(dialCode: Int?): Country = entries.firstOrNull { it.dialCode == dialCode } ?: Default

        fun search(query: String): List<Country> {
            val digits = query.trim().removePrefix("+")
            return entries.filter { it.dialCode.toString().startsWith(digits) }
        }
    }
}
