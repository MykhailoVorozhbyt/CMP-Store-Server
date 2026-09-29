package plugins.capability

import extensions.composeDep
import extensions.libs
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

private const val UI_TEST_SRC_DIR = "src/uiTest/kotlin"
private const val JVM_TEST = "jvmTest"
private const val ANDROID_DEVICE_TEST = "androidDeviceTest"

class FeatureUiTestCapabilityPlugin : CapabilityPlugin() {

    override fun KotlinMultiplatformExtension.configureCapability(target: Project) {
        sourceSets.configureEach {
            when (name) {
                JVM_TEST -> {
                    kotlin.srcDir(UI_TEST_SRC_DIR)
                    dependencies {
                        implementation(target.libs.junit.ui.test)
                        implementation(target.composeDep.desktop.currentOs)
                    }
                }
                ANDROID_DEVICE_TEST -> {
                    kotlin.srcDir(UI_TEST_SRC_DIR)
                    dependencies {
                        implementation(target.libs.androidx.test.runner)
                        implementation(target.libs.compose.ui.test.manifest)
                    }
                }
            }
        }
    }
}
