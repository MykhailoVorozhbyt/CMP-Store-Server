package extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

inline fun Project.appExtension(
    crossinline configure: AppExtension.() -> Unit
) = extensions.configure<AppExtension> { configure() }

inline fun Project.baseAppModuleExtension(
    crossinline configure: BaseAppModuleExtension.() -> Unit
) = extensions.configure<BaseAppModuleExtension> { configure() }

inline fun ComposeExtension.desktopExtension(
    crossinline action: DesktopExtension.() -> Unit
) = extensions.configure<DesktopExtension> { action() }

inline fun Project.composeExtension(
    crossinline configure: ComposeExtension.() -> Unit
) = extensions.configure<ComposeExtension> { configure() }

inline fun Project.kotlinMultiplatformExtension(
    crossinline configure: KotlinMultiplatformExtension.() -> Unit
) = extensions.configure<KotlinMultiplatformExtension> { configure() }

inline fun Project.javaLibraryExtension(
    crossinline configure: JavaPluginExtension.() -> Unit
) = extensions.configure<JavaPluginExtension> { configure() }

inline fun Project.libraryExtension(
    crossinline configure: LibraryExtension.() -> Unit
) = extensions.configure<LibraryExtension> { configure() }

//inline fun Project.detektExtension(
//    crossinline configure: DetektExtension.() -> Unit
//) = extensions.configure<DetektExtension> { configure() }

inline fun Project.applicationExtension(
    crossinline configure: ApplicationExtension.() -> Unit
) = extensions.configure<ApplicationExtension> { configure() }

//inline fun Project.crashlyticsExtension(
//    crossinline configure: CrashlyticsExtension.() -> Unit
//) = extensions.configure<CrashlyticsExtension> { configure() }

//inline fun Project.hiltExtension(
//    crossinline configure: HiltExtension.() -> Unit
//) = extensions.configure<HiltExtension> { configure() }

//inline fun Project.appDistributionExtension(
//    crossinline configure: AppDistributionExtension.() -> Unit
//) = extensions.configure<AppDistributionExtension> { configure() }

//inline fun Project.composeCompilerExtension(
//    crossinline configure: ComposeCompilerGradlePluginExtension.() -> Unit
//) = extensions.configure<ComposeCompilerGradlePluginExtension> { configure() }

inline fun Project.applyPlugins(crossinline plugin: () -> List<String>) {
    pluginManager.apply {
        plugin().forEach {
            apply(plugin = it)
        }
    }
}