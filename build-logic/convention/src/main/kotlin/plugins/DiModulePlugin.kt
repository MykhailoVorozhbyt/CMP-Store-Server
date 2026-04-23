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
import utils.enums.ModulePath
import utils.enums.PluginName

class DiModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@DiModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId,
                libs.plugin(PluginName.KOIN_COMPILER.pName).pluginId,
                libs.plugin(PluginName.KSP.pName).pluginId,
            )
        }
        kotlinMultiplatformExtension {
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.SHARED.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(project(ModulePath.CORE_NAVIGATION.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DATA.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DOMAIN.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_PRESENTATION.path))

                    implementation(library(LibraryName.KOIN_CORE))
                    implementation(library(LibraryName.KOIN_COMPOSE_VIEWMODEL))
                    implementation(library(LibraryName.KOIN_COMPOSE_NAVIGATION3))
                    implementation(library(LibraryName.JETBRAINS_NAVIGATION_3_UI))
                    implementation(library(LibraryName.KTOR_CLIENT_CORE))
                }
                commonTest.dependencies {
                    implementation(library(LibraryName.KOIN_TEST))
                }
            }
        }

        configureAndroidLibraryBase(ModuleName.DI.mName)
    }
}