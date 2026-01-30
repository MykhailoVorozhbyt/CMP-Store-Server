package extensions

import org.gradle.api.Project

/**
 * Extension to get Android SDK versions from version catalog
 */
fun Project.getAndroidSdkVersions(): AndroidSdkVersions {
    return AndroidSdkVersions(
        compileSdk = libs.version("android-compileSdk").requiredVersion.toInt(),
        minSdk = libs.version("android-minSdk").requiredVersion.toInt(),
        targetSdk = libs.version("android-targetSdk").requiredVersion.toInt(),
        versionCode = getVersionCode(),
        versionName = getDefaultVersionName(),
    )
}

/**
version format is X.YYY.Z
X - api changes
Y - new version (regular tasks)
Z - Hot Fixes (default 0)
 **/
const val versionMajor = 1
const val versionMinor = 0
const val versionBuild = 0

fun getVersionCode(): Int {
    return versionMajor * 10000 + versionMajor * 100 + versionMinor
}

fun getDefaultVersionName(): String {
    return "${versionMajor}.${versionMinor}.${versionBuild}"
}

/**
 * Data class to hold Android SDK version information
 */
data class AndroidSdkVersions(
    val compileSdk: Int,
    val minSdk: Int,
    val targetSdk: Int,
    val versionCode: Int,
    val versionName: String,
)