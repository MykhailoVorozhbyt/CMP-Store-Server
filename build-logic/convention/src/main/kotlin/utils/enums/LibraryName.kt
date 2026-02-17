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
    ANDROIDX_CORE_SPLASHSCREEN("androidx-core-splashscreen"),

    KOTLINX_COROUTINES_SWING("kotlinx-coroutinesSwing"),

    LOGBACK("logback"),

    KTOR_SERVER_CORE("ktor-serverCore"),
    KTOR_SERVER_NETTY("ktor-serverNetty"),
    KTOR_SERVER_TEST_HOST("ktor-serverTestHost"),

    KOIN_CORE("koin-core"),
    KOIN_BOM("io.insert-koin:koin-bom:4.2.0"),
    KOIN_ANDROID("koin-android"),
    KOIN_COMPOSE("koin-compose"),
    KOIN_COMPOSE_VIEWMODEL("koin-compose-viewmodel"),
    KOIN_TEST("koin-test"),


    COMPOSE_UI("compose-ui"),
    COMPOSE_RUNTIME("compose-runtime"),
    COMPOSE_FOUNDATION("compose-foundation"),
    COMPOSE_COMPONENTS_RESOURCES("compose-components-resources"),
    COMPOSE_UI_TOOLING("compose-ui-tooling"),
    COMPOSE_UI_TOOLING_PREVIEW("compose-ui-tooling-preview"),
    COMPOSE_MATERIAL_3("compose-material3");

    companion object {
        fun Project.library(lib: LibraryName) = libs.library(lib.lName).get()
    }
}