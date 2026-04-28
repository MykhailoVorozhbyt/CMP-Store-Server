package plugins.feature

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.applyPlugins
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


class FeatureHomeDataModulePlugin : FeatureHomeModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.HOME_DATA

    override fun apply(target: Project): Unit = with(target) project@{
        super.apply(target)
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.FEATURE_HOME_DOMAIN.path))
                    implementation(library(LibraryName.KTOR_CLIENT_CORE))
                    implementation(library(LibraryName.KTOR_CLIENT_CONTENT_NEGOTIATION))
                    implementation(library(LibraryName.KTOR_SERIALIZATION_KOTLINX_JSON))
                }
                androidMain.dependencies {
                    implementation(library(LibraryName.KTOR_CLIENT_OKHTTP))
                }
                iosMain.dependencies {
                    implementation(library(LibraryName.KTOR_CLIENT_DARWIN))
                }
                jvmMain.dependencies {
                    implementation(library(LibraryName.KTOR_CLIENT_OKHTTP))
                }
            }
        }
    }
}

class FeatureHomeDomainModulePlugin : FeatureHomeModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.HOME_DOMAIN
}

class FeatureHomePresentationModulePlugin : FeatureHomeModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.HOME_PRESENTATION

    override fun apply(target: Project): Unit = with(target) project@{
        super.apply(target)
        applyPlugins {
            listOf(libs.plugin(PluginName.STORE_COMPOSE_MULTIPLATFORM.pName).pluginId)
        }
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.CORE_RESOURCES.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(project(ModulePath.CORE_UTILS.path))
                    implementation(project(ModulePath.FEATURE_HOME_DOMAIN.path))

                    implementation(library(LibraryName.ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE))
                    implementation(library(LibraryName.ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE))

                    implementation(library(LibraryName.COMPOSE_UI))
                    implementation(library(LibraryName.COMPOSE_RUNTIME))
                    implementation(library(LibraryName.COMPOSE_FOUNDATION))
                    implementation(library(LibraryName.COMPOSE_MATERIAL_3))
                    implementation(library(LibraryName.COMPOSE_COMPONENTS_RESOURCES))
                    implementation(library(LibraryName.COMPOSE_UI_TOOLING_PREVIEW))

                    implementation(library(LibraryName.KOIN_CORE))
                    implementation(library(LibraryName.KOIN_COMPOSE))
                    implementation(library(LibraryName.KOIN_COMPOSE_VIEWMODEL))
                }
            }
        }
        dependencies.add("androidRuntimeClasspath", library(LibraryName.COMPOSE_UI_TOOLING))
    }
}

abstract class FeatureHomeModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@FeatureHomeModulePlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.STORE_KOTLIN_MULTIPLATFORM.pName).pluginId
            )
        }
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.SHARED.path))
                }
            }
        }
    }
}