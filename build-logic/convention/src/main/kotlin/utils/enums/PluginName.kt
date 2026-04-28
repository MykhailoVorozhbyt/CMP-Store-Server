package utils.enums

import extensions.libs
import extensions.plugin
import org.gradle.api.Project

enum class PluginName(val pName: String) {
    ANDROID_APPLICATION("androidApplication"),
    ANDROID_LIBRARY("androidLibrary"),

    COMPOSE_MULTIPLATFORM("composeMultiplatform"),
    COMPOSE_COMPILER("composeCompiler"),
    COMPOSE_HOT_RELOAD("composeHotReload"),

    KOTLIN_JVM("kotlinJvm"),
    KOTLIN_MULTIPLATFORM("kotlinMultiplatform"),
    KOTLIN_MULTIPLATFORM_LIBRARY("kotlinMultiplatformLibrary"),

    KTOR("ktor"),
    KOIN_COMPILER("koin-compiler"),
    SERIALIZATION("serialization"),
    GOOGLE_SERVICES("google-services"),
    KSP("ksp"),

    //Gradle Convention Plugins
    STORE_KOTLIN_MULTIPLATFORM("store-kotlinMultiplatform"),
    STORE_COMPOSE_MULTIPLATFORM("store-composeMultiplatform"),
    STORE_SHARED("store-shared"),
    STORE_COMPOSE_APP("store-composeApp");

    companion object {
        fun Project.plugin(lib: PluginName) = libs.plugin(lib.pName).pluginId
    }

}