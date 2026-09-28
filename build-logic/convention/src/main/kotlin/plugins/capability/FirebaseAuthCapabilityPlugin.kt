package plugins.capability

import extensions.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class FirebaseAuthCapabilityPlugin : CapabilityPlugin() {

    override fun KotlinMultiplatformExtension.configureCapability(target: Project) {
        sourceSets {
            commonMain.dependencies {
                implementation(target.dependencies.platform(target.libs.firebase.bom))
                implementation(target.libs.kmpauth.firebase)
            }
        }
    }
}
