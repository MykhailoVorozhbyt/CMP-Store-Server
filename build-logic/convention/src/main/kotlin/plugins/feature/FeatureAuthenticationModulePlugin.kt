package plugins.feature

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.androidRuntimeClasspath
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ModuleName
import utils.enums.ModulePath

class FeatureAuthenticationDataModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_DATA

    override fun apply(target: Project): Unit = with(target) project@{
        super.apply(target)
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DOMAIN.path))
                    implementation(libs.ktor.clientCore)
                    implementation(libs.ktor.clientContentNegotiation)
                    implementation(libs.ktor.serializationKotlinxJson)
                }
                androidMain.dependencies {
                    implementation(libs.ktor.clientOkHttp)
                }
                iosMain.dependencies {
                    implementation(libs.ktor.clientDarwin)
                }
                jvmMain.dependencies {
                    implementation(libs.ktor.clientOkHttp)
                }
                commonTest.dependencies {
                    implementation(libs.kotlin.test)
                    implementation(libs.ktor.clientMock)
                }
            }
        }
    }
}

class FeatureAuthenticationDomainModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_DOMAIN
}

class FeatureAuthenticationPresentationModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_PRESENTATION

    override fun apply(target: Project): Unit = with(target) project@{
        super.apply(target)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)
        pluginManager.alias(libs.plugins.stability.analyzer)
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.CORE_NAVIGATION.path))
                    implementation(project(ModulePath.CORE_RESOURCES.path))
                    implementation(project(ModulePath.CORE_PRESENTATION.path))
                    implementation(project(ModulePath.CORE_UTILS.path))
                    implementation(project(ModulePath.FEATURE_AUTHENTICATION_DOMAIN.path))

                    implementation(libs.compose.ui)
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.components.resources)
                    implementation(libs.compose.ui.tooling.preview)
                    implementation(libs.jetbrains.navigation3.ui)

                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.koin.compose.viewmodel)

                    implementation(libs.kmpauth.google)
                }
            }
        }
        dependencies.androidRuntimeClasspath(libs.compose.ui.tooling)
    }
}

abstract class FeatureAuthenticationModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@FeatureAuthenticationModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    implementation(project(ModulePath.SHARED.path))

                    implementation(libs.androidx.lifecycle.viewmodelCompose)
                    implementation(libs.androidx.lifecycle.runtimeCompose)

                    implementation(project.dependencies.platform(libs.firebase.bom))
                    implementation(libs.kmpauth.firebase)
                }
            }
        }
    }
}
