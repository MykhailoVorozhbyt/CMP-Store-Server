package configuration

import com.android.build.api.dsl.androidLibrary
import extensions.androidExtension
import extensions.getAndroidSdkVersions
import extensions.kotlinMultiplatformExtension
import org.gradle.api.Project
import utils.currentJvmTarget

fun Project.configureAndroidBase(namespace: String) = this.androidExtension {
    val sdk = getAndroidSdkVersions()
    this.namespace = namespace
    this.compileSdk = sdk.compileSdk
}


fun Project.configureAndroidLibraryBase(namespace: String) = this.kotlinMultiplatformExtension {
    androidLibrary {
        val sdk = getAndroidSdkVersions()
        this.namespace = namespace
        this.compileSdk = sdk.compileSdk
        this.minSdk = sdk.minSdk

        compilerOptions {
            jvmTarget.set(currentJvmTarget)
        }

        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }
}