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
import utils.enums.ModulePath
import utils.enums.PluginName

class CoreNavigationModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@CoreNavigationModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId,
                libs.plugin(PluginName.SERIALIZATION.pName).pluginId,
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.NAVIGATION.mName)
            configureIOS()
            jvm()
            kotlinMultiplatformExtension {
                sourceSets {
                    commonMain.dependencies {
                        implementation(project(ModulePath.SHARED.path))
                        implementation(project(ModulePath.CORE_PRESENTATION.path))
                        implementation(library(LibraryName.JETBRAINS_NAVIGATION_3_UI))
                        implementation(library(LibraryName.KOIN_COMPOSE_NAVIGATION3))
                        implementation(library(LibraryName.JETBRAINS_MATERIAL_3_ADAPTIVE_NAVIGATION_3))
                        implementation(library(LibraryName.JETBRAINS_LIFECYCLE_VIEWMODEL_NAVIGATION_3))
                        implementation(library(LibraryName.KOTLINX_SERIALIZATION_JSON))
                    }
                }
            }
        }
    }
}