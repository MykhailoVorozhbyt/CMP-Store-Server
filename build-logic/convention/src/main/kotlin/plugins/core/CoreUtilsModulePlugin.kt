package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.applyPlugins
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName

class CoreUtilsModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@CoreUtilsModulePlugin} invoked ***")
        applyPlugins {
            listOf(libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId)
        }
        kotlinMultiplatformExtension {
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING_PREVIEW))
                }
            }
        }

        configureAndroidLibraryBase(ModuleName.CORE_UTILS.mName)

    }
}