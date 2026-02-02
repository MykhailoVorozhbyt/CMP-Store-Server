package plugins.stores

import configuration.composeDesktopApplication
import configuration.configureAndroidBase
import configuration.configureCompileOptions
import configuration.configureFlavors
import extensions.applyPlugins
import extensions.baseAppModuleExtension
import extensions.composeDep
import extensions.getAndroidSdkVersions
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.moduleName
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.currentJvmTarget
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName


class AndroidAthleticaPlusModulePlugin : AndroidStoresModulePlugin() {
    override val applicationIdName: String = "com.store.athletica_plus"
    override val applicationName: String = "Athletica Plus"
    override val mainClass = "com.store.athletica_plus.MainKt"
    override val packageName = "com.store.athletica_plus"
    override val appName = "Athletica Plus"
    override val appVersion = "1.0.0"
}

class AndroidNutriSportModulePlugin : AndroidStoresModulePlugin() {
    override val applicationIdName: String = "com.store.nutri_sport"
    override val applicationName: String = "Nutri Sport"
    override val mainClass = "com.store.nutri_sport.MainKt"
    override val packageName = "com.store.nutri_sport"
    override val appName = "Nutri Sport"
    override val appVersion = "1.0.0"
}

abstract class AndroidStoresModulePlugin : Plugin<Project> {
    abstract val applicationIdName: String
    abstract val applicationName: String
    abstract val mainClass: String
    abstract val packageName: String
    abstract val appName: String
    abstract val appVersion: String

    override fun apply(target: Project) = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.ANDROID_APPLICATION.pName).pluginId,
                libs.plugin(PluginName.KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.STORE_COMPOSE_MULTIPLATFORM.pName).pluginId,
            )
        }
        kotlinMultiplatformExtension {
            androidTarget {
                compilerOptions {
                    jvmTarget.set(currentJvmTarget)
                }
            }
            jvm()
            listOf(
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = moduleName
                    isStatic = true
                }
            }

            sourceSets {
                androidMain.dependencies {
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING))
                    implementation(library(LibraryName.ANDROIDX_ACTIVITY_COMPOSE))
                    implementation(library(LibraryName.KOIN_ANDROID))
                }
                commonMain.dependencies {
                    implementation(project(":composeApp"))
                    implementation(project(":core:presentation"))
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING_PREVIEW))
                    implementation(library(LibraryName.KOIN_CORE))
                }
                jvmMain.dependencies {
                    implementation(composeDep.desktop.currentOs)
                }
            }
        }

        configureAndroid()

        composeDesktopApplication(
            mainClass = mainClass,
            packageName = packageName,
            version = appVersion,
        )
    }


    private fun Project.configureAndroid() {
        baseAppModuleExtension {
            configureAndroidBase(ModuleName.STORES.mName)
            val sdk = getAndroidSdkVersions()
            defaultConfig {
                applicationId = applicationIdName
                minSdk = sdk.minSdk
                targetSdk = sdk.targetSdk
                versionCode = sdk.versionCode
                versionName = sdk.versionName
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                manifestPlaceholders.putAll(
                    mapOf("label" to applicationName),
//                    mapOf("icon" to icon),
//                    mapOf("roundIcon" to roundIcon)
                )
            }
            buildTypes {
                release {
                    isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                        .map(String::toBooleanStrict).getOrElse(true)
                    isShrinkResources = true
                    isDebuggable = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
                debug {
                    isMinifyEnabled = false
                    isShrinkResources = false
                    isDebuggable = true
                    applicationIdSuffix = ".debug"
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
            configureFlavors(this)
            packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
            configureCompileOptions()
        }
    }

}