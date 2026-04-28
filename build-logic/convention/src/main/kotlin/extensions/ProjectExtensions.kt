package extensions

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

//TODO: use form agb9 - android
fun Project.android(configure: Action<ApplicationExtension>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)

inline fun Project.desktopExtension(
    crossinline action: DesktopExtension.() -> Unit
) = { -> composeExtension { extensions.configure<DesktopExtension> { action() } } }

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

/**
 * For the future*/
/*
inline fun Project.detektExtension(
    crossinline configure: DetektExtension.() -> Unit
) = extensions.configure<DetektExtension> { configure() }

inline fun Project.crashlyticsExtension(
    crossinline configure: CrashlyticsExtension.() -> Unit
) = extensions.configure<CrashlyticsExtension> { configure() }

inline fun Project.composeCompilerExtension(
    crossinline configure: ComposeCompilerGradlePluginExtension.() -> Unit
) = extensions.configure<ComposeCompilerGradlePluginExtension> { configure() }
*/

inline fun Project.applyPlugins(crossinline plugin: () -> List<String>) {
    pluginManager.apply {
        plugin().forEach {
            apply(plugin = it)
        }
    }
}