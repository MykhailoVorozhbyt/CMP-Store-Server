package configuration

import extensions.kotlinMultiplatformExtension
import extensions.moduleName
import org.gradle.api.Project

fun Project.configureIOS() = kotlinMultiplatformExtension {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = moduleName
            isStatic = true
        }
        iosTarget.compilerOptions {
            freeCompilerArgs.add("-Xexport-kdoc")
        }
    }
}