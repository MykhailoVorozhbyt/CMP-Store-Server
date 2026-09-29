package configuration

import extensions.kotlinMultiplatformExtension
import org.gradle.api.Project

fun Project.configureIOS(frameworkName: String? = null) = kotlinMultiplatformExtension {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        if (frameworkName != null) {
            iosTarget.binaries.framework {
                baseName = frameworkName
                isStatic = true
            }
        }
        iosTarget.compilerOptions {
            freeCompilerArgs.add("-Xexport-kdoc")
        }
    }
}
