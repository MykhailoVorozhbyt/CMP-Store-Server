package extensions

import org.gradle.api.Project

/**
 * Extension to get Android SDK versions from version catalog
 */
fun Project.getAndroidSdkVersions(): AndroidSdkVersions {
    return AndroidSdkVersions(
        compileSdk = libs.version("android-compileSdk").requiredVersion.toInt(),
        minSdk = libs.version("android-minSdk").requiredVersion.toInt(),
        targetSdk = libs.version("android-targetSdk").requiredVersion.toInt()
    )
}

/**
 * Data class to hold Android SDK version information
 */
data class AndroidSdkVersions(
    val compileSdk: Int,
    val minSdk: Int,
    val targetSdk: Int
)