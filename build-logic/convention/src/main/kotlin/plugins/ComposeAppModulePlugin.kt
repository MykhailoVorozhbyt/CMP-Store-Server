package plugins

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.androidRuntimeClasspath
import extensions.composeDep
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class ComposeAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@ComposeAppModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)
        pluginManager.alias(libs.plugins.ksp)

        dependencies.androidRuntimeClasspath(libs.compose.ui.tooling)
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(ModuleName.APP.mName)
            configureIOS()
            jvm()

            sourceSets {
                androidMain.dependencies {
                    implementation(libs.androidx.activity.compose)
                    implementation(libs.androidx.core.splashscreen)
                    implementation(libs.androidx.customview.customview)
                }
                commonMain.dependencies {
                    implementation(project(ModulePath.DI.path))
                    implementation(project(ModulePath.SHARED.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(project(ModulePath.CORE_UTILS.path))
                    implementation(project(ModulePath.CORE_RESOURCES.path))
                    implementation(project(ModulePath.CORE_NAVIGATION.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DOMAIN.path))

                    implementation(libs.compose.ui)
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.compose.components.resources)

                    implementation(libs.jetbrains.navigation3.ui)

                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.koin.compose.viewmodel)

                    implementation(project.dependencies.platform(libs.firebase.bom))
                    implementation(libs.firebase.app)
                    implementation(libs.kmpauth.google)
                }
                commonTest.dependencies {
                    implementation(libs.kotlin.test)
                }
                jvmMain.dependencies {
                    implementation(composeDep.desktop.currentOs)
                    implementation(libs.kotlinx.coroutinesSwing)
                }
            }
        }
    }
}
