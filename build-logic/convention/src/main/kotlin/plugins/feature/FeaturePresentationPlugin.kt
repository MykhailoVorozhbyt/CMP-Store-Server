package plugins.feature

import configuration.configureAndroidLibraryBase
import configuration.configureIOS
import extensions.alias
import extensions.androidRuntimeClasspath
import extensions.component
import extensions.derivedNamespace
import extensions.kotlinMultiplatformExtension
import extensions.libs
import extensions.module
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.enums.ComponentLayer
import utils.enums.FeatureName
import utils.enums.ModulePath

private const val FEATURE_ROOT = "feature"

class FeaturePresentationPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@FeaturePresentationPlugin} invoked ***")
        val feature = owningFeature()
        pluginManager.alias(libs.plugins.store.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.store.composeMultiplatform)
        pluginManager.alias(libs.plugins.stability.analyzer)

        kotlinMultiplatformExtension {
            configureAndroidLibraryBase(derivedNamespace)
            configureIOS()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    module(ModulePath.SHARED)
                    module(ModulePath.CORE_DOMAIN)
                    module(ModulePath.CORE_PRESENTATION)
                    module(ModulePath.CORE_NAVIGATION)
                    module(ModulePath.CORE_RESOURCES)
                    module(ModulePath.CORE_UTILS)
                    feature.components.forEach { name ->
                        module(component(name, ComponentLayer.USECASE))
                        module(component(name, ComponentLayer.MODEL))
                    }

                    implementation(libs.kotlinx.coroutines.core)
                    implementation(libs.kotlinx.collections.immutable)

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

                    implementation(libs.koin.core)
                    implementation(libs.koin.compose)
                    implementation(libs.koin.compose.viewmodel)
                }
                commonTest.dependencies {
                    module(ModulePath.TEST)
                    implementation(libs.compose.ui.test)
                }
            }
        }
        dependencies.androidRuntimeClasspath(libs.compose.ui.tooling)
    }

    private fun Project.owningFeature(): FeatureName {
        val segments = path.split(":").filter(String::isNotBlank)
        if (segments.size != 2 || segments[0] != FEATURE_ROOT) {
            throw GradleException(
                "The feature presentation plugin expects a module at ':$FEATURE_ROOT:<name>', but was applied to '$path'."
            )
        }
        return FeatureName.fromDirName(segments[1]) ?: throw GradleException(
            "Feature '${segments[1]}' ($path) is not listed in FeatureName — add it there with its components."
        )
    }
}
