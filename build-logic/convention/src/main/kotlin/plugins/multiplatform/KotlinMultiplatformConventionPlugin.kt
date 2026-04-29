package plugins.multiplatform

import extensions.alias
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@KotlinMultiplatformConventionPlugin} invoked ***")
        pluginManager.alias(libs.plugins.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.kotlinMultiplatformLibrary)
    }
}
