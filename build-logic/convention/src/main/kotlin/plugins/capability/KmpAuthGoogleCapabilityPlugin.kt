package plugins.capability

import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KmpAuthGoogleCapabilityPlugin : CapabilityPlugin() {

    override fun KotlinMultiplatformExtension.configureCapability(target: Project) {
        sourceSets {
            commonMain.dependencies {
                implementation(target.libs.kmpauth.google)
            }
        }
    }
}
