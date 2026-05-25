package utils.enums

enum class ModuleName(val mName: String) {
    APP("org.cmp.store"),
    SHARED("org.cmp.store.shared"),
    DI("com.store.di"),
    ATHLETICA_PLUS_KMP("com.store.athletica_plus.kmp"),
    NUTRI_SPORT_KMP("com.store.nutri_sport.kmp"),
    CORE_PRESENTATION("com.store.core.presentation"),
    CORE_UTILS("com.store.core.utils"),
    CORE_RESOURCES("com.store.core.resources"),
    CORE_DOMAIN("com.store.core.domain"),
    CORE_DATA("com.store.core.data"),
    CORE_SECURITY("com.store.core.security"),
    CORE_NETWORK("com.store.core.network"),
    AUTHENTICATION_DATA("com.store.feature.authentication.data"),
    AUTHENTICATION_DOMAIN("com.store.feature.authentication.domain"),
    AUTHENTICATION_PRESENTATION("com.store.feature.authentication.presentation"),
    HOME_DATA("com.store.feature.home.data"),
    HOME_DOMAIN("com.store.feature.home.domain"),
    HOME_PRESENTATION("com.store.feature.home.presentation"),
    NAVIGATION("com.store.navigation"),
    TEST("com.store.test"),
}