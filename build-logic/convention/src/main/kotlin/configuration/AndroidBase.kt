package configuration

import extensions.androidLibrary
import extensions.getAndroidSdkVersions
import extensions.kotlinMultiplatformExtension
import org.gradle.api.Project
import utils.currentJvmTarget

fun Project.configureAndroidLibraryBase(
    namespace: String,
    enableAndroidResources: Boolean = true,
) = this.kotlinMultiplatformExtension {
    androidLibrary {
        val sdk = getAndroidSdkVersions()
        this.namespace = namespace
        this.compileSdk = sdk.compileSdk
        this.minSdk = sdk.minSdk

        compilerOptions {
            jvmTarget.set(currentJvmTarget)
        }
        androidResources {
            enable = enableAndroidResources
        }
        withJava()
        withHostTestBuilder {}.configure {
            isReturnDefaultValues = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
}