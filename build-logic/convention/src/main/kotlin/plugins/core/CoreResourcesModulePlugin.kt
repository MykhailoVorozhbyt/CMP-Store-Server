package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.applyPlugins
import extensions.composeExtension
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.compose.resources.ResourcesExtension
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName

class CoreResourcesModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@CoreResourcesModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_RESOURCES.mName)
            configureIOS()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
                }
                commonMain.dependencies {
                    implementation(library(LibraryName.COMPOSE_RUNTIME))
                    implementation(library(LibraryName.COMPOSE_FOUNDATION))
                    implementation(library(LibraryName.COMPOSE_MATERIAL_3))
                    implementation(library(LibraryName.COMPOSE_COMPONENTS_RESOURCES))
                }
            }
        }
        composeExtension {
            extensions.configure<ResourcesExtension> {
                publicResClass = true
                packageOfResClass = "com.store.core.resources"
                generateResClass = always
            }
        }
    }
}