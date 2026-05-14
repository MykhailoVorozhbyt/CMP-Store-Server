import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatformLibrary) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.stability.analyzer) apply false
}

tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = JavaLanguageVersion.of(21)
}
