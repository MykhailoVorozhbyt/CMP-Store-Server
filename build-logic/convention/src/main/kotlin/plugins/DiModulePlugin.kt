package plugins

import configuration.configureAndroidLibraryBase
import extensions.applyPlugins
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.declarativedsl.intrinsics.listOf
import org.gradle.kotlin.dsl.invoke
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName

class DiModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@DiModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId,
            )
        }
        kotlinMultiplatformExtension {
            iosArm64()
            iosSimulatorArm64()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(library(LibraryName.KOIN_ANDROID))
                }
                commonMain.dependencies {
                    implementation(project(":shared"))

                    implementation(library(LibraryName.KOIN_CORE))
                    implementation(library(LibraryName.KOIN_COMPOSE))
                    implementation(library(LibraryName.KOIN_COMPOSE_VIEWMODEL))
                }
                commonTest.dependencies {
                    implementation(library(LibraryName.KOIN_TEST))
                }
            }
        }

        configureAndroidLibraryBase(ModuleName.DI.mName)
    }
}