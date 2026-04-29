package plugins.multiplatform

import extensions.alias
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@ComposeMultiplatformConventionPlugin} invoked ***")
        pluginManager.alias(libs.plugins.composeMultiplatform)
        pluginManager.alias(libs.plugins.composeCompiler)
    }
}
