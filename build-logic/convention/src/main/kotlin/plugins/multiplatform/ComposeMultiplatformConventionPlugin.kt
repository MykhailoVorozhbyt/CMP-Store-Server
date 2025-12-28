package plugins.multiplatform

import extensions.applyPlugins
import extensions.libs
import extensions.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.enums.PluginName

class ComposeMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@ComposeMultiplatformConventionPlugin} invoked ***")
        applyPlugins {
            listOf(
                libs.plugin(PluginName.COMPOSE_MULTIPLATFORM.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_COMPILER.pName).pluginId,
                libs.plugin(PluginName.COMPOSE_HOT_RELOAD.pName).pluginId,
            )
        }
    }
}