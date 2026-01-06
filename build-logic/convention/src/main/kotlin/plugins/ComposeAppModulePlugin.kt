package plugins

import configuration.composeDesktopApplication
import configuration.configureCompileOptions
import extensions.applyPlugins
import extensions.baseAppModuleExtension
import extensions.composeDep
import extensions.debugImplementation
import extensions.getAndroidSdkVersions
import extensions.kotlinMultiplatformExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName


class ComposeAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@ComposeAppModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                "org.jetbrains.kotlin.multiplatform",
                "com.android.application",
                "store.kotlin.composeMultiplatform"
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
            jvm()

            targets.withType<KotlinNativeTarget>().configureEach {
                binaries.framework {
                    baseName = "ComposeApp"
                    isStatic = true
                }
            }

            sourceSets {
                androidMain.dependencies {
                    implementation(composeDep.preview)
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
                }
                commonMain.dependencies {
                    implementation(composeDep.runtime)
                    implementation(composeDep.foundation)
                    implementation(composeDep.material3)
                    implementation(composeDep.ui)
                    implementation(composeDep.components.resources)
                    implementation(composeDep.components.uiToolingPreview)
                    implementation(library(LibraryName.ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE))
                    implementation(library(LibraryName.ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE))
                    implementation(project(":shared"))
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

            packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                }
                getByName("debug") {
                    isMinifyEnabled = false
                }
            }

            configureCompileOptions()
        }
        // Dependencies
        dependencies {
            debugImplementation(composeDep.uiTooling)
        }
        // Desktop Compose
        composeDesktopApplication(
            mainClass = "org.cmp.store.MainKt",
            packageName = "org.cmp.store",
            version = "1.0.0",
        )
    }
}