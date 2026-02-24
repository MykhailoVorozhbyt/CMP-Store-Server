rootProject.name = "CMP-Store-Server"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
include(":composeApp")
include(":server")
include(":shared")
include(":stores")
include(":stores:athletica-plus")
include(":stores:nutri-sport")
include(":di")
include(":core")
include(":core:presentation")
include(":core:utils")
include(":core:resources")
include(":feature")
include(":feature:authentication:data")
include(":feature:authentication:domain")
include(":feature:authentication:presentation")
