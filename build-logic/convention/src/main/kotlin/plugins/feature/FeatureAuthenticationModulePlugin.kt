package plugins.feature

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.androidRuntimeClasspath
import extensions.composeDep
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
import extensions.sourceSet
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
        pluginManager.alias(libs.plugins.serialization)
        kotlinMultiplatformExtension {
            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.FEATURE_AUTHENTICATION_DOMAIN)
                    module(ModulePath.CORE_UTILS)
                    module(ModulePath.CORE_SECURITY)
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
                    module(ModulePath.TEST)
                    implementation(libs.kotlin.test)
                    implementation(libs.ktor.clientMock)
                    implementation(libs.kotlinx.coroutines.test)
                }
            }
        }
    }
}

class FeatureAuthenticationDomainModulePlugin : FeatureAuthenticationModulePlugin() {
    override val moduleName: ModuleName
        get() = ModuleName.AUTHENTICATION_DOMAIN

    override fun apply(target: Project): Unit = with(target) project@{
        pluginManager.alias(libs.plugins.serialization)
        super.apply(target)
        kotlinMultiplatformExtension {
            sourceSets {
                commonTest.dependencies {
                    module(ModulePath.TEST)
                    implementation(libs.kotlin.test)
                    implementation(libs.kotlinx.coroutines.test)
                }
            }
        }
    }
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
                    module(ModulePath.CORE_NAVIGATION)
                    module(ModulePath.CORE_RESOURCES)
                    module(ModulePath.CORE_PRESENTATION)
                    module(ModulePath.CORE_UTILS)
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

                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.koin.compose.viewmodel)

                    implementation(libs.kmpauth.google)
                }
                commonTest.dependencies {
                    module(ModulePath.TEST)
                    implementation(libs.compose.ui.test)
                }

                /**
                 * The Compose UI test lives in src/uiTest (NOT commonTest): commonTest also flows into
                 * androidHostTest, where runComposeUiTest has no actual and NPEs. Sharing the same
                 * src/uiTest via kotlin.srcDir keeps it on exactly the two targets with a real runner —
                 * jvmTest (headless skiko) and androidDeviceTest (emulator) — and off androidHostTest.
                 * */
                sourceSet("androidDeviceTest", srcDir = "src/uiTest/kotlin") {
                    implementation(libs.androidx.test.runner)
                    implementation(libs.compose.ui.test.manifest)
                }
                sourceSet("jvmTest", srcDir = "src/uiTest/kotlin") {
                    implementation(libs.junit.ui.test)
                    implementation(composeDep.desktop.currentOs)
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
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_NETWORK)
                    module(ModulePath.CORE_DOMAIN)

                    implementation(libs.kotlinx.coroutines.core)

                    implementation(project.dependencies.platform(libs.firebase.bom))
                    implementation(libs.kmpauth.firebase)
                }
            }
        }
    }
}