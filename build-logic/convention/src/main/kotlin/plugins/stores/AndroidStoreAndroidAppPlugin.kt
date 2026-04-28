package plugins.stores

import configuration.configureCompileOptions
import extensions.android
import extensions.applyPlugins
import extensions.getAndroidSdkVersions
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import utils.currentJvmTarget
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModulePath
import utils.enums.PluginName


class AthleticaPlusAndroidAppPlugin : StoreAndroidAppPlugin() {
    override val applicationId = "com.store.athletica_plus"
    override val applicationName = "Athletica Plus"
    override val storeKmpModulePath = ModulePath.STORES_ATHLETICA_PLUS
}

class NutriSportAndroidAppPlugin : StoreAndroidAppPlugin() {
    override val applicationId = "com.store.nutri_sport"
    override val applicationName = "Nutri Sport"
    override val storeKmpModulePath = ModulePath.STORES_NUTRI_SPORT
}

abstract class StoreAndroidAppPlugin : Plugin<Project> {
    abstract val applicationId: String
    abstract val applicationName: String
    abstract val storeKmpModulePath: ModulePath

    override fun apply(target: Project): Unit = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.ANDROID_APPLICATION.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId,
                libs.plugin(PluginName.GOOGLE_SERVICES.pName).pluginId,
            )
        }

        android {
            val sdk = getAndroidSdkVersions()
            namespace = applicationId
            compileSdk = sdk.compileSdk
            defaultConfig {
                this.applicationId = this@StoreAndroidAppPlugin.applicationId
                minSdk = sdk.minSdk
                targetSdk = sdk.targetSdk
                versionCode = sdk.versionCode
                versionName = sdk.versionName
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                manifestPlaceholders.putAll(mapOf("label" to applicationName))
            }
            buildTypes {
                release {
                    isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                        .map(String::toBooleanStrict).getOrElse(true)
                    isShrinkResources = true
                    isDebuggable = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
                debug {
                    isDebuggable = true
                    proguardFiles(
                        getDefaultProguardFile("proguard-android-optimize.txt"),
                        "proguard-rules.pro"
                    )
                }
            }
            packaging {
                resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
            configureCompileOptions()
        }

        tasks.withType(KotlinCompile::class.java).configureEach {
            compilerOptions {
                jvmTarget.set(currentJvmTarget)
            }
        }

        dependencies.add("implementation", project(storeKmpModulePath.path))
        dependencies.add("implementation", project(ModulePath.COMPOSE_APP.path))
        dependencies.add("implementation", library(LibraryName.COMPOSE_RUNTIME))
        dependencies.add("implementation", library(LibraryName.KOIN_ANDROID))
    }
}