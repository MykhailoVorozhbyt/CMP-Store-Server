package plugins.stores

import configuration.composeDesktopApplication
import extensions.applyPlugins
import extensions.composeDep
import extensions.implementation
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import utils.enums.PluginName

class DesktopAthleticaPlusModulePlugin : DesktopStoresModulePlugin() {
    override val mainClass = "com.store.athletica_plus_jvm.MainKt"
    override val packageName = "com.store.athletica_plus_jvm"
    override val appName = "Athletica Plus"
    override val appVersion = "1.0.0"
}

class DesktopNutriSportModulePlugin : DesktopStoresModulePlugin() {
    override val mainClass = "com.store.nutri_sport_jvm.MainKt"
    override val packageName = "com.store.nutri_sport_jvm"
    override val appName = "Nutri Sport"
    override val appVersion = "1.0.0"
}

abstract class DesktopStoresModulePlugin : Plugin<Project> {

    abstract val mainClass: String
    abstract val packageName: String
    abstract val appName: String
    abstract val appVersion: String

    override fun apply(target: Project) = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.KOTLIN_JVM.pName).pluginId,
                libs.plugin(PluginName.STORE_COMPOSE_MULTIPLATFORM.pName).pluginId,
            )
        }
        dependencies {
            implementation(project(":composeApp"))
            implementation(composeDep.desktop.currentOs)
        }
        composeDesktopApplication(
            mainClass = mainClass,
            packageName = packageName,
            version = appVersion,
        )
    }
}
