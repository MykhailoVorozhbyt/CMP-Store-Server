package plugins

import com.android.build.api.dsl.androidLibrary
import extensions.getAndroidSdkVersions
import extensions.kotlinMultiplatformExtension
import extensions.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.invoke
import utils.currentJvmTarget
import utils.enums.LibraryName
import utils.enums.LibraryName.Companion.library
import utils.enums.ModuleName

class SharedModulePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        kotlinMultiplatformExtension {
            val sdk = getAndroidSdkVersions()
            androidLibrary {
                namespace = ModuleName.SHARED.mName
                compileSdk = sdk.compileSdk
                minSdk = sdk.minSdk

                compilerOptions {
                    jvmTarget.set(currentJvmTarget)
                }
            }

            iosArm64()
            iosSimulatorArm64()
            jvm()

            sourceSets {
                commonMain.dependencies {
                    // shared deps
                }
                commonTest.dependencies {
                    implementation(library(LibraryName.KOTLIN_TEST))
                }
            }
        }
    }
}