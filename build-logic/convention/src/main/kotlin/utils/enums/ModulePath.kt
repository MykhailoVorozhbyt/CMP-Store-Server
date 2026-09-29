package utils.enums

enum class ModulePath(val path: String) {
    COMPOSE_APP(":composeApp"),
    SERVER(":server"),
    SHARED(":shared"),
    STORES_ATHLETICA_PLUS(":stores:athletica-plus"),
    STORES_NUTRI_SPORT(":stores:nutri-sport"),
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