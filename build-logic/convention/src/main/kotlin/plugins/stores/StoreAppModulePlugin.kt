package plugins.stores

import configuration.configureAndroidBase
import configuration.configureCompileOptions
import configuration.configureFlavors
import configuration.configureKotlin
import extensions.androidTestImplementation
import extensions.applyPlugins
import extensions.baseAppModuleExtension
import extensions.composeDep
import extensions.debugImplementation
import extensions.getAndroidSdkVersions
import extensions.implementation
import extensions.libs
import extensions.plugin
import extensions.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName
import utils.enums.PluginName


class AthleticaPlusModulePlugin : StoresModulePlugin() {
    override val applicationIdName: String = "com.store.athletica_plus"
    override val applicationName: String = "Athletica Plus"

}

class NutriSportModulePlugin : StoresModulePlugin() {
    override val applicationIdName: String = "com.store.nutri_sport"
    override val applicationName: String = "Nutri Sport"
}

abstract class StoresModulePlugin : Plugin<Project> {
    abstract val applicationIdName: String
    abstract val applicationName: String

    override fun apply(target: Project) = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.ANDROID_APPLICATION.pName).pluginId,
                libs.plugin(PluginName.KOTLIN_ANDROID.pName).pluginId,
            )
        }
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
                    mapOf(
                        "label" to applicationName
                    )
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
//            testOptions.unitTests.isIncludeAndroidResources = true
            configureCompileOptions()
            configureKotlin<KotlinAndroidProjectExtension>()
        }
        moduleDependencies()
    }

    protected fun Project.moduleDependencies() {
        dependencies {
            implementation(project(":composeApp"))
            testImplementation(library(LibraryName.JUNIT))
            androidTestImplementation(library(LibraryName.ANDROIDX_TEST_EXT_JUNIT))
            androidTestImplementation(library(LibraryName.ANDROIDX_ESPRESSO_CORE))
            debugImplementation(composeDep.uiTooling)
        }
    }
}