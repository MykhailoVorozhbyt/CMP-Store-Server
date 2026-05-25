package plugins

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

class DiModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        println("*** ${this@DiModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.composeMultiplatform)
        pluginManager.alias(libs.plugins.composeCompiler)
        pluginManager.alias(libs.plugins.koin.compiler)
        pluginManager.alias(libs.plugins.ksp)

        kotlinMultiplatformExtension {
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.SHARED.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(project(ModulePath.CORE_NAVIGATION.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DATA.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DOMAIN.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_PRESENTATION.path))
                    implementation(project(ModulePath.FEATURE_HOME_DATA.path))
                    implementation(project(ModulePath.FEATURE_HOME_DOMAIN.path))
                    implementation(project(ModulePath.FEATURE_HOME_PRESENTATION.path))

                    implementation(libs.koin.core)
                    implementation(libs.koin.compose.viewmodel)
                    implementation(libs.koin.compose.navigation3)
                    implementation(libs.jetbrains.navigation3.ui)
                    implementation(libs.ktor.clientCore)
                }
                commonTest.dependencies {
                    implementation(libs.koin.test)
                }
            }
        }

        configureAndroidLibraryBase(ModuleName.DI.mName)
    }
}
