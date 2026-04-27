package utils.enums

enum class ModuleName(val mName: String) {
    APP("org.cmp.store"),
    SHARED("org.cmp.store.shared"),
    STORES("com.stores.store"),
    DI("com.store.di"),
    CORE_PRESENTATION("com.store.core.presentation"),
    CORE_UTILS("com.store.core.utils"),
    CORE_RESOURCES("com.store.core.resources"),
    AUTHENTICATION_DATA("com.store.feature.authentication.data"),
    AUTHENTICATION_DOMAIN("com.store.feature.authentication.domain"),
    AUTHENTICATION_PRESENTATION("com.store.feature.authentication.presentation"),
    HOME_DATA("com.store.feature.home.data"),
    HOME_DOMAIN("com.store.feature.home.domain"),
    HOME_PRESENTATION("com.store.feature.home.presentation"),
    NAVIGATION("com.store.navigation"),
}