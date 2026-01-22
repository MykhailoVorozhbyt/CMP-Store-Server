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

class SharedModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@SharedModulePlugin} invoked ***")
        applyPlugins { listOf(libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId) }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.SHARED.mName)
            iosArm64()
            iosSimulatorArm64()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    // shared deps
                }
                commonTest.dependencies {
                    implementation(library(LibraryName.KOTLIN_TEST))
                }
            }
        }
    }
}