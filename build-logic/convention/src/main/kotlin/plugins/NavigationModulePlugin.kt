package plugins

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

class NavigationModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@NavigationModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.NAVIGATION.mName)
            configureIOS()
            jvm()
            kotlinMultiplatformExtension {
                sourceSets {
                    androidMain.dependencies {

                    }
                    commonMain.dependencies {
                        implementation(library(LibraryName.JETBRAINS_NAVIGATION_3_UI))
                        implementation(library(LibraryName.JETBRAINS_MATERIAL_3_ADAPTIVE_NAVIGATION_3))
                        implementation(library(LibraryName.JETBRAINS_LIFECYCLE_VIEWMODEL_NAVIGATION_3))
                    }
                }
            }
        }
    }
}