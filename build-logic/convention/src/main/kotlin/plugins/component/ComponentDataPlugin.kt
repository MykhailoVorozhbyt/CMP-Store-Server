package plugins.component

import extensions.alias
import extensions.component
import extensions.libs
import extensions.module
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import utils.enums.ComponentLayer
import utils.enums.ComponentName
import utils.enums.ModulePath

class ComponentDataPlugin : ComponentLayerPlugin(ComponentLayer.DATA) {

    override fun KotlinMultiplatformExtension.configureLayer(target: Project, owner: ComponentName) {
        target.pluginManager.alias(target.libs.plugins.serialization)
        sourceSets {
            commonMain.dependencies {
                module(component(owner, ComponentLayer.MODEL))
                module(component(owner, ComponentLayer.DOMAIN_API))
                module(ModulePath.CORE_NETWORK)
                module(ModulePath.CORE_SECURITY)
                module(ModulePath.CORE_UTILS)
                implementation(target.libs.ktor.clientCore)
                implementation(target.libs.ktor.clientContentNegotiation)
                implementation(target.libs.ktor.serializationKotlinxJson)
                implementation(target.libs.kotlinx.serialization.json)
                implementation(target.libs.koin.core)
            }
            androidMain.dependencies {
                implementation(target.libs.ktor.clientOkHttp)
            }
            iosMain.dependencies {
                implementation(target.libs.ktor.clientDarwin)
            }
            jvmMain.dependencies {
                implementation(target.libs.ktor.clientOkHttp)
            }
            commonTest.dependencies {
                module(ModulePath.TEST)
                implementation(target.libs.ktor.clientMock)
            }
        }
    }
}
