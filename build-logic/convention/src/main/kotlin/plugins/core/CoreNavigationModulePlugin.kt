package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class CoreNavigationModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@CoreNavigationModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.composeMultiplatform)
        pluginManager.alias(libs.plugins.composeCompiler)
        pluginManager.alias(libs.plugins.serialization)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.NAVIGATION.mName)
            configureIOS()
            jvm()
            kotlinMultiplatformExtension {
                sourceSets {
                    commonMain.dependencies {
                        implementation(project(ModulePath.SHARED.path))
                        implementation(project(ModulePath.CORE_PRESENTATION.path))
                        implementation(libs.jetbrains.navigation3.ui)
                        implementation(libs.koin.compose.navigation3)
                        implementation(libs.jetbrains.material3.adaptiveNavigation3)
                        implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)
                        implementation(libs.kotlinx.serialization.json)
                    }
                }
            }
        }
    }
}
