package plugins

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
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
            configureAndroidLibraryBase(ModuleName.DI.mName)
            configureIOS()
            jvm()

            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_NETWORK)
                    module(ModulePath.CORE_PRESENTATION)
                    module(ModulePath.CORE_NAVIGATION)
                    module(ModulePath.CORE_DOMAIN)
                    module(ModulePath.CORE_SECURITY)
                    module(ModulePath.FEATURE_AUTHENTICATION_DATA)
                    module(ModulePath.FEATURE_AUTHENTICATION_DOMAIN)
                    module(ModulePath.FEATURE_AUTHENTICATION_PRESENTATION)
                    module(ModulePath.FEATURE_HOME_DATA)
                    module(ModulePath.FEATURE_HOME_DOMAIN)
                    module(ModulePath.FEATURE_HOME_PRESENTATION)

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
    }
}
