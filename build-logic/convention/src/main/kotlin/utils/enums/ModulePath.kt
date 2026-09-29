package utils.enums

enum class ModulePath(val path: String) {
    APP_SHARED(":app:shared"),
    SERVER(":server"),
    SHARED(":shared"),
    APP_ATHLETICA_PLUS(":app:athletica-plus"),
    APP_NUTRI_SPORT(":app:nutri-sport"),
    DI(":di"),
    CORE_PRESENTATION(":core:presentation"),
    CORE_UTILS(":core:utils"),
    CORE_RESOURCES(":core:resources"),
    CORE_NAVIGATION(":core:navigation"),
    CORE_DATA(":core:data"),
    CORE_DOMAIN(":core:domain"),
    CORE_SECURITY(":core:security"),
    CORE_NETWORK(":core:network"),
    FEATURE_AUTHENTICATION(":feature:authentication"),
    FEATURE_HOME(":feature:home"),
    TEST(":test"),
}