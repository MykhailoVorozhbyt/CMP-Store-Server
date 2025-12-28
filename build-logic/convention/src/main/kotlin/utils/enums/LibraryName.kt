package utils.enums

import extensions.library
import extensions.libs
import org.gradle.api.Project

enum class LibraryName(val lName: String) {
    KOTLIN_TEST("kotlin-test"),
    KOTLIN_TEST_JUNIT("kotlin-testJunit"),
    JUNIT("junit"),

    ANDROIDX_CORE_KTX("androidx-core-ktx"),
    ANDROIDX_TEST_EXT_JUNIT("androidx-testExt-junit"),
    ANDROIDX_ESPRESSO_CORE("androidx-espresso-core"),
    ANDROIDX_APPCOMPAT("androidx-appcompat"),
    ANDROIDX_ACTIVITY_COMPOSE("androidx-activity-compose"),
    ANDROIDX_LIFECYCLE_VIEWMODEL_COMPOSE("androidx-lifecycle-viewmodelCompose"),
    ANDROIDX_LIFECYCLE_RUNTIME_COMPOSE("androidx-lifecycle-runtimeCompose"),

    KOTLINX_COROUTINES_SWING("kotlinx-coroutinesSwing"),

    LOGBACK("logback"),

    KTOR_SERVER_CORE("ktor-serverCore"),
    KTOR_SERVER_NETTY("ktor-serverNetty"),
    KTOR_SERVER_TEST_HOST("ktor-serverTestHost");

    companion object {
        fun Project.library(lib: LibraryName) = libs.library(lib.lName).get()
    }
}