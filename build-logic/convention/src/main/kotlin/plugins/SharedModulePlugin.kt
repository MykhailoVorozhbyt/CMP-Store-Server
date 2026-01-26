package plugins

import com.android.build.api.dsl.androidLibrary
import configuration.configureAndroidLibraryBase
import extensions.applyPlugins
import extensions.composeExtension
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.declarativedsl.intrinsics.listOf
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.compose.resources.ResourcesExtension
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName

class SharedModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@SharedModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.SHARED.mName)
            androidLibrary {
                androidResources.enable = true
            }
            iosArm64()
            iosSimulatorArm64()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
                }
                commonMain {
                    resources.srcDir("src/commonMain/composeResources")
                }
                commonMain.dependencies {
                    implementation(library(LibraryName.COMPOSE_RUNTIME))
                    implementation(library(LibraryName.COMPOSE_FOUNDATION))
                    implementation(library(LibraryName.COMPOSE_MATERIAL_3))
                    implementation(library(LibraryName.COMPOSE_COMPONENTS_RESOURCES))
                }
                commonTest.dependencies {
                    implementation(library(LibraryName.KOTLIN_TEST))
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