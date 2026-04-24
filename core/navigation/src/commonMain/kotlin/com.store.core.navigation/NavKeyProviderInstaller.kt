package com.store.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

fun interface NavKeyProviderInstaller<T : NavKey> {
    fun build(builder: PolymorphicModuleBuilder<T>)
}
