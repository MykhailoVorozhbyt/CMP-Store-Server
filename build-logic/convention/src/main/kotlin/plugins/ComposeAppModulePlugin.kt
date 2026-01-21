package plugins

import configuration.configureAndroidLibraryBase
import extensions.applyPlugins
import extensions.composeDep
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.moduleName
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName


class ComposeAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@ComposeAppModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.STORE_COMPOSE_MULTIPLATFORM.pName).pluginId,
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.APP.mName)

            iosArm64()
            iosSimulatorArm64()
            jvm()

            targets.withType<KotlinNativeTarget>().configureEach {
                binaries.framework {
                    baseName = moduleName
                    isStatic = true
                }
            }

            sourceSets {
                androidMain.dependencies {
                    implementation(composeDep.preview)
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
//                    implementation(libs.koin.android)
//                    implementation(libs.koin.compose)
                }
                commonMain.dependencies {
                    implementation(project(":shared"))
                    implementation(project(":core:presentation"))
                    implementation(composeDep.runtime)
                    implementation(composeDep.foundation)
                    implementation(composeDep.material3)
                    implementation(composeDep.ui)
                    implementation(composeDep.components.resources)
                    implementation(composeDep.components.uiToolingPreview)
                    implementation(library(LibraryName.ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE))
                    implementation(library(LibraryName.ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE))

//                    implementation(libs.koin.core)

                }

                commonTest.dependencies {
                    implementation(library(LibraryName.KOTLIN_TEST))
//                    implementation(libs.koin.test)
                }
                jvmMain.dependencies {
                    implementation(composeDep.desktop.currentOs)
                    implementation(library(LibraryName.KOTLINX_COROUTINES_SWING))
                }
            }
        }
    }
}