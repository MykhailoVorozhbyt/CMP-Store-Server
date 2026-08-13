package plugins.feature

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

class FeatureHomeDataModulePlugin : FeatureHomeModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.HOME_DATA

    override fun apply(target: Project): Unit = with(target) project@{
        super.apply(target)
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.FEATURE_HOME_DOMAIN)
                    implementation(libs.ktor.clientCore)
                    implementation(libs.ktor.clientContentNegotiation)
                    implementation(libs.ktor.serializationKotlinxJson)
                    implementation(libs.koin.core)
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
                    implementation(libs.kotlinx.coroutines.test)
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
        pluginManager.alias(libs.plugins.store.composeMultiplatform)
        pluginManager.alias(libs.plugins.stability.analyzer)
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.CORE_NAVIGATION)
                    module(ModulePath.CORE_RESOURCES)
                    module(ModulePath.CORE_PRESENTATION)
                    module(ModulePath.CORE_UTILS)
                    module(ModulePath.FEATURE_HOME_DOMAIN)
                    module(ModulePath.FEATURE_AUTHENTICATION_DOMAIN)

                    implementation(libs.androidx.lifecycle.viewmodelCompose)
                    implementation(libs.androidx.lifecycle.runtimeCompose)

                    implementation(libs.compose.ui)
                    implementation(libs.compose.runtime)
                    implementation(libs.compose.foundation)
                    implementation(libs.compose.material3)
                    implementation(libs.compose.components.resources)
                    implementation(libs.compose.ui.tooling.preview)

                    implementation(libs.jetbrains.navigation3.ui)
                    implementation(libs.jetbrains.material3.adaptiveLayout)

                    implementation(libs.kotlinx.collections.immutable)

                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.koin.compose.viewmodel)
                }
            }
        }
        dependencies.androidRuntimeClasspath(libs.compose.ui.tooling)
    }
}

abstract class FeatureHomeModulePlugin : Plugin<Project> {
    abstract val moduleName: ModuleName
    override fun apply(target: Project): Unit = with(target) project@{
        println("*** ${this@FeatureHomeModulePlugin} invoked ***")
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(moduleName.mName)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_NETWORK)
                    module(ModulePath.CORE_DOMAIN)
                    implementation(libs.kotlinx.coroutines.core)
                }
            }
        }
    }
}
