package plugins.component

import extensions.apiModule
import extensions.component
import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.enums.ComponentLayer
import utils.enums.ComponentName

class ComponentDomainApiPlugin : ComponentLayerPlugin(ComponentLayer.DOMAIN_API) {

    override fun KotlinMultiplatformExtension.configureLayer(target: Project, owner: ComponentName) {
        sourceSets {
            commonMain.dependencies {
                apiModule(component(owner, ComponentLayer.MODEL))
                api(target.libs.kotlinx.coroutines.core)
            }
        }
    }
}
