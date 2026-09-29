package plugins.component

import extensions.apiModule
import extensions.component
import extensions.libs
import extensions.module
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.enums.ComponentLayer
import utils.enums.ComponentName
import utils.enums.ModulePath

class ComponentUseCasePlugin : ComponentLayerPlugin(ComponentLayer.USECASE) {

    override fun KotlinMultiplatformExtension.configureLayer(target: Project, owner: ComponentName) {
        sourceSets {
            commonMain.dependencies {
                apiModule(component(owner, ComponentLayer.MODEL))
                module(component(owner, ComponentLayer.DOMAIN_API))
                api(target.libs.kotlinx.coroutines.core)
                implementation(target.libs.koin.core)
            }
            commonTest.dependencies {
                module(ModulePath.TEST)
            }
        }
    }
}
