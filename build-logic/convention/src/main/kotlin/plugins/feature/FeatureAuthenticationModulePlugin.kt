package plugins.feature

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.applyPlugins
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.enums.ModuleName
import utils.enums.PluginName


class FeatureAuthenticationDataModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_DATA

}

class FeatureAuthenticationDomainModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_DOMAIN

}

class FeatureAuthenticationPresentationModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_PRESENTATION

}

abstract class FeatureAuthenticationModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@FeatureAuthenticationModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS()
            jvm()
        }
    }
}