package plugins.stores

import configuration.configureAndroidLibraryBase
import configuration.configureDesktopApplication
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


class AppAthleticaPlusModulePlugin : StoreModulePlugin() {
    override val moduleName: ModuleName = ModuleName.ATHLETICA_PLUS_KMP
    override val mainClass = "com.store.athletica_plus.MainKt"
    override val packageName = "com.store.athletica_plus"
    override val appVersion = "1.0.0"
}

class AppNutriSportModulePlugin : StoreModulePlugin() {
    override val moduleName: ModuleName = ModuleName.NUTRI_SPORT_KMP
    override val mainClass = "com.store.nutri_sport.MainKt"
    override val packageName = "com.store.nutri_sport"
    override val appVersion = "1.0.0"
}

abstract class StoreModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    abstract val mainClass: String
    abstract val packageName: String
    abstract val appVersion: String

    override fun apply(target: Project) = with(target) {
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.STORE_COMPOSE_MULTIPLATFORM.pName).pluginId,
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.COMPOSE_APP.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(library(LibraryName.COMPOSE_COMPONENTS_RESOURCES))
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING_PREVIEW))
                    implementation(library(LibraryName.KOIN_CORE))
                    implementation(library(LibraryName.KOIN_COMPOSE))
                    implementation(library(LibraryName.FIREBASE_APP))
                    implementation(library(LibraryName.KMPAUTH_GOOGLE))
                }
                androidMain.dependencies {
                    implementation(project.dependencies.platform(library(LibraryName.FIREBASE_BOM)))
                }
                jvmMain.dependencies {
                    implementation(composeDep.desktop.currentOs)
                }
            }
        }

        configureDesktopApplication(
            mainClass = mainClass,
            packageName = packageName,
            version = appVersion,
        )
    }
}