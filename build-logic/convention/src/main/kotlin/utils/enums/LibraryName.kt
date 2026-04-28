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
    ANDROIDX_CUSTOMVIEW("androidx-customview-customview"),

    KOTLINX_COROUTINES_SWING("kotlinx-coroutinesSwing"),

    LOGBACK("logback"),

    KTOR_SERVER_CORE("ktor-serverCore"),
    KTOR_SERVER_NETTY("ktor-serverNetty"),
    KTOR_SERVER_TEST_HOST("ktor-serverTestHost"),
    KTOR_SERVER_CONTENT_NEGOTIATION("ktor-serverContentNegotiation"),
    KTOR_SERIALIZATION_KOTLINX_JSON("ktor-serializationKotlinxJson"),
    KTOR_CLIENT_CORE("ktor-clientCore"),
    KTOR_CLIENT_CONTENT_NEGOTIATION("ktor-clientContentNegotiation"),
    KTOR_CLIENT_OKHTTP("ktor-clientOkHttp"),
    KTOR_CLIENT_DARWIN("ktor-clientDarwin"),
    KTOR_CLIENT_LOGGING("ktor-clientLogging"),

    KOIN_CORE("koin-core"),
    KOIN_BOM("io.insert-koin:koin-bom:4.2.0"),
    KOIN_ANDROID("koin-android"),
    KOIN_COMPOSE("koin-compose"),
    KOIN_COMPOSE_VIEWMODEL("koin-compose-viewmodel"),
    KOIN_COMPOSE_NAVIGATION3("koin-compose-navigation3"),
    KOIN_ANNOTATIONS("koin-annotations"),
    KOIN_KSP_COMPILER("koin-ksp-compiler"),
    KOIN_TEST("koin-test"),

    JETBRAINS_NAVIGATION_3_UI("jetbrains-navigation3-ui"),
    JETBRAINS_MATERIAL_3_ADAPTIVE_NAVIGATION_3("jetbrains-material3-adaptiveNavigation3"),
    JETBRAINS_LIFECYCLE_VIEWMODEL_NAVIGATION_3("jetbrains-lifecycle-viewmodelNavigation3"),

    COMPOSE_UI("compose-ui"),
    COMPOSE_RUNTIME("compose-runtime"),
    COMPOSE_FOUNDATION("compose-foundation"),
    COMPOSE_COMPONENTS_RESOURCES("compose-components-resources"),
    COMPOSE_UI_TOOLING("compose-ui-tooling"),
    COMPOSE_UI_TOOLING_PREVIEW("compose-ui-tooling-preview"),
    COMPOSE_MATERIAL_3("compose-material3"),

    KOTLINX_SERIALIZATION_JSON("kotlinx-serialization-json"),

    KMPAUTH_GOOGLE("kmpauth-google"),
    KMPAUTH_FIREBASE("kmpauth-firebase"),
    KMPAUTH_FIREBASE_FACEBOOK("kmpauth-firebase-facebook"),

    FIREBASE_BOM("firebase-bom"),
    FIREBASE_APP("firebase-app"),
    FIREBASE_FIRESTORE("firebase-firestore"),
    FIREBASE_STORAGE("firebase-storage"),
    FIREBASE_COMMON("firebase-common");

    companion object {
        fun Project.library(lib: LibraryName) = libs.library(lib.lName).get()
    }
}