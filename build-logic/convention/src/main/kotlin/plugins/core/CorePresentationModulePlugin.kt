package plugins.core

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.androidRuntimeClasspath
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class CorePresentationModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@CorePresentationModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)
        pluginManager.alias(libs.plugins.serialization)

        dependencies.androidRuntimeClasspath(libs.compose.ui.tooling)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.CORE_PRESENTATION.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_RESOURCES)
                    module(ModulePath.CORE_UTILS)
                    module(ModulePath.CORE_DOMAIN)

                    implementation(libs.kotlinx.serialization.json)

                    implementation(libs.compose.ui)
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.components.resources)
                    implementation(libs.compose.ui.tooling.preview)

                    implementation(libs.jetbrains.navigation3.ui)

                    implementation(libs.androidx.lifecycle.viewmodelCompose)

                    implementation(libs.koin.compose)
                }
                commonTest.dependencies {
                    module(ModulePath.TEST)
                    implementation(libs.kotlin.test)
                    implementation(libs.ktor.clientMock)
                    implementation(libs.kotlinx.coroutines.test)
                }
            }
        }
    }
}
