/*
package plugins.app

import configuration.composeDesktopApplication
import configuration.configureCompileOptions
import extensions.applyDependencies
import extensions.applyPlugins
import extensions.baseAppModuleExtension
import extensions.getAndroidSdkVersions
import extensions.kotlinMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.compose
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName

class ComposeAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@ComposeAppModulePlugin} invoked ***")
        // Plugins
        applyPlugins {
            listOf(
                PluginName.ANDROID_APPLICATION.pName,
                PluginName.KOTLIN_MULTIPLATFORM.pName,
                PluginName.COMPOSE_MULTIPLATFORM.pName,
            )
        }
        // Kotlin Multiplatform
        kotlinMultiplatformExtension {

            androidTarget {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_11)
                }
            }

            iosArm64()
            iosSimulatorArm64()

            targets.withType<KotlinNativeTarget>()
                .configureEach {
                    binaries.framework {
                        baseName = "ComposeApp"
                        isStatic = true
                    }
                }

            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(this@kotlinMultiplatformExtension.compose.runtime)
                    implementation(this@kotlinMultiplatformExtension.compose.foundation)
                    implementation(this@kotlinMultiplatformExtension.compose.material3)
                    implementation(this@kotlinMultiplatformExtension.compose.ui)
                    implementation(this@kotlinMultiplatformExtension.compose.components.resources)
                    implementation(this@kotlinMultiplatformExtension.compose.components.uiToolingPreview)
                    this@project.applyDependencies(
                        listOf(
                            LibraryName.ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE,
                            LibraryName.ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE
                        )
                    )
                    implementation(project(":shared"))
                }

                commonMain.dependencies {
                    implementation(library(LibraryName.KOTLIN_TEST))
                }

                commonTest.dependencies {
                    implementation(this@kotlinMultiplatformExtension.compose.preview)
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
                }

                jvmMain.dependencies {
                    implementation(this@kotlinMultiplatformExtension.compose.desktop.currentOs)
                    implementation(library(LibraryName.KOTLINX_COROUTINES_SWING))
                }
            }
        }

        // Android
        baseAppModuleExtension {
            namespace = ModuleName.APP.mName

            val sdk = getAndroidSdkVersions()
            compileSdkVersion(sdk.compileSdk)
            defaultConfig {
                applicationId = ModuleName.APP.mName
                minSdk = sdk.minSdk
                targetSdk = sdk.targetSdk
                versionCode = sdk.versionCode
                versionName = sdk.versionName
            }

            packagingOptions {
                resources.excludes += setOf(
                    "/META-INF/AL2.0",
                    "/META-INF/LGPL2.1"
                )
            }

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                }
            }

            configureCompileOptions()
        }
        // Desktop Compose
        composeDesktopApplication(
            mainClass = "org.cmp.store.MainKt",
            packageName = "org.cmp.store",
            version = "1.0.0",
        )
        // Dependencies
        dependencies {
            add("debugImplementation", compose.uiTooling)
        }
    }
}
*/
