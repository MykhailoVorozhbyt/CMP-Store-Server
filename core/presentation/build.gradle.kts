import configuration.configureAndroidLibraryBase

plugins {
    alias(libs.plugins.store.kotlinMultiplatform)
    alias(libs.plugins.store.composeMultiplatform)
}

kotlin {
    configureAndroidLibraryBase("com.store.core.presentation")

    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material3)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
        }
    }
}
