package plugins.component

import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.enums.ComponentLayer
import utils.enums.ComponentName
import utils.enums.ModulePath

class ComponentModelPlugin : ComponentLayerPlugin(ComponentLayer.MODEL) {

    override fun KotlinMultiplatformExtension.configureLayer(target: Project, owner: ComponentName) {
        sourceSets {
            commonMain.dependencies {
                api(project(ModulePath.SHARED.path))
                api(project(ModulePath.CORE_DOMAIN.path))
            }
        }
    }
}
