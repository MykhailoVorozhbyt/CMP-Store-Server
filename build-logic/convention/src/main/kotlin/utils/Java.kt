package utils

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val targetCompatibilityVersion = JavaVersion.VERSION_17
val sourceCompatibilityVersion = JavaVersion.VERSION_17
val currentJvmTarget = JvmTarget.JVM_17