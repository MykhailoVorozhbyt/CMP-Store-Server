package plugins.multiplatform

import extensions.alias
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.JVM_TOOLCHAIN_VERSION

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        println("*** ${this@KotlinMultiplatformConventionPlugin} invoked ***")
        pluginManager.alias(libs.plugins.kotlinMultiplatform)
        pluginManager.alias(libs.plugins.kotlinMultiplatformLibrary)

        kotlinMultiplatformExtension {
            jvmToolchain(JVM_TOOLCHAIN_VERSION)
            compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
}
