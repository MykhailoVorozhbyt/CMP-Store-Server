package plugins.capability

import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

abstract class CapabilityPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@CapabilityPlugin} invoked ***")
        pluginManager.withPlugin(libs.plugins.kotlinMultiplatform.get().pluginId) {
            kotlinMultiplatformExtension {
                configureCapability(target)
            }
        }
    }

    protected abstract fun KotlinMultiplatformExtension.configureCapability(target: Project)
}
