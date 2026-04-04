plugins {
    alias(libs.plugins.store.android.nutriSport)
}
android {
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}
