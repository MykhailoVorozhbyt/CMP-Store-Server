package utils.enums

enum class PluginName(val pName: String) {
    ANDROID_APPLICATION("androidApplication"),

    COMPOSE_MULTIPLATFORM("composeMultiplatform"),
    COMPOSE_COMPILER("composeCompiler"),
    COMPOSE_HOT_RELOAD("composeHotReload"),

    KOTLIN_JVM("kotlinJvm"),
    KOTLIN_ANDROID("kotlinAndroid"),
    KOTLIN_MULTIPLATFORM("kotlinMultiplatform"),
    KOTLIN_MULTIPLATFORM_LIBRARY("kotlinMultiplatformLibrary"),

    KTOR("ktor")
}