package extensions

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.accessors.runtime.extensionOf
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

val Project.libs: LibrariesForLibs
    get() = extensionOf(this, "libs") as LibrariesForLibs

fun Project.android(configure: Action<ApplicationExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)

inline fun Project.desktopExtension(
    crossinline action: DesktopExtension.() -> Unit
) = composeExtension { extensions.configure<DesktopExtension> { action() } }

inline fun Project.composeExtension(
    crossinline configure: ComposeExtension.() -> Unit
) = extensions.configure<ComposeExtension> { configure() }

inline fun Project.kotlinMultiplatformExtension(
    crossinline configure: KotlinMultiplatformExtension.() -> Unit
) = extensions.configure<KotlinMultiplatformExtension> { configure() }

inline fun Project.applicationExtension(
    crossinline configure: ApplicationExtension.() -> Unit
) = extensions.configure<ApplicationExtension> { configure() }

val Project.moduleName
    get() = path
        .split(":")
        .filter { it.isNotBlank() }
        .joinToString("") { it.capitalized() }

val Project.modulePackageName
    get() = path
        .split(":")
        .filter { it.isNotBlank() }
        .joinToString(".") { it.lowercase() }

fun PluginManager.alias(notation: Provider<PluginDependency>) {
    apply(notation.get().pluginId)
}

fun PluginManager.alias(notation: ProviderConvertible<PluginDependency>) {
    apply(notation.asProvider().get().pluginId)
}
