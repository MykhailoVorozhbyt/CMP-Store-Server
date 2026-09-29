package plugins.app

import configuration.configureDesktopApplication
import extensions.alias
import extensions.composeDep
import extensions.implementation
import extensions.libs
import extensions.module
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import utils.currentJvmTarget
import utils.enums.ModulePath
import utils.sourceCompatibilityVersion
import utils.targetCompatibilityVersion

class AthleticaPlusDesktopAppPlugin : StoreDesktopAppPlugin() {
    override val mainClass = "com.store.athletica_plus.MainKt"
    override val packageName = "com.store.athletica_plus"
    override val storeKmpModulePath = ModulePath.APP_ATHLETICA_PLUS
}

class NutriSportDesktopAppPlugin : StoreDesktopAppPlugin() {
    override val mainClass = "com.store.nutri_sport.MainKt"
    override val packageName = "com.store.nutri_sport"
    override val storeKmpModulePath = ModulePath.APP_NUTRI_SPORT
}

abstract class StoreDesktopAppPlugin : Plugin<Project> {
    abstract val mainClass: String
    abstract val packageName: String
    abstract val storeKmpModulePath: ModulePath
    open val appVersion = "1.0.0"

    override fun apply(target: Project): Unit = with(target) {
        pluginManager.alias(libs.plugins.kotlinJvm)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)

        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = sourceCompatibilityVersion
            targetCompatibility = targetCompatibilityVersion
        }
        tasks.withType(KotlinCompile::class.java).configureEach {
            compilerOptions {
                jvmTarget.set(currentJvmTarget)
            }
        }

        dependencies {
            module(storeKmpModulePath)
            module(ModulePath.APP_SHARED)
            implementation(composeDep.desktop.currentOs)
            implementation(libs.compose.components.resources)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.koin.core)
            add("runtimeOnly", libs.logback)
        }

        configureDesktopApplication(
            mainClass = mainClass,
            packageName = packageName,
            version = appVersion,
        )
    }
}
