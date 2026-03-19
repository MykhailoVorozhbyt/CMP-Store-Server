package plugins

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.applyPlugins
import extensions.composeDep
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


class ComposeAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@ComposeAppModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.STORE_COMPOSE_MULTIPLATFORM.pName).pluginId
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.APP.mName)
            configureIOS(library(LibraryName.KMP_NOTIFIER))
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING))
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
                    implementation(library(LibraryName.ANDROIDX_CORE_SPLASHSCREEN))
                    implementation(library(LibraryName.ANDROIDX_CUSTOMVIEW))
                    implementation(library(LibraryName.ANDROIDX_CUSTOMVIEW_POOLINGCONTAINER))
                    implementation(library(LibraryName.ANDROIDX_EMOJI_2))
                }
                commonMain.dependencies {
                    implementation(project(ModulePath.DI.path))
                    implementation(project(ModulePath.SHARED.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(project(ModulePath.CORE_UTILS.path))
                    implementation(project(ModulePath.CORE_RESOURCES.path))
                    implementation(project(ModulePath.CORE_NAVIGATION.path))

                    implementation(library(LibraryName.COMPOSE_UI))
                    implementation(library(LibraryName.COMPOSE_RUNTIME))
                    implementation(library(LibraryName.COMPOSE_FOUNDATION))
                    implementation(library(LibraryName.COMPOSE_MATERIAL_3))
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING_PREVIEW))
                    implementation(library(LibraryName.COMPOSE_COMPONENTS_RESOURCES))

                    implementation(library(LibraryName.KOIN_CORE))
                    implementation(library(LibraryName.KOIN_COMPOSE))

                    implementation(project.dependencies.platform(library(LibraryName.FIREBASE_BOM)))
                    implementation(library(LibraryName.FIREBASE_APP))
                    implementation(library(LibraryName.KMPAUTH_GOOGLE))

                    api(library(LibraryName.KMP_NOTIFIER))
                }
                commonTest.dependencies {
                    implementation(library(LibraryName.KOTLIN_TEST))
                }
                jvmMain.dependencies {
                    implementation(composeDep.desktop.currentOs)
                    implementation(library(LibraryName.KOTLINX_COROUTINES_SWING))
                }
            }
        }
    }
}