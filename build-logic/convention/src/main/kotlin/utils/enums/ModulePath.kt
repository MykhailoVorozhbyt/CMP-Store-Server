package utils.enums

enum class ModulePath(val path: String) {
    COMPOSE_APP(":composeApp"),
    SERVER(":server"),
    SHARED(":shared"),
    DI(":di"),
    CORE_PRESENTATION(":core:presentation"),
    CORE_UTILS(":core:utils"),
    CORE_RESOURCES(":core:resources"),
    CORE_NAVIGATION(":core:navigation"),
    FEATURE_AUTHENTICATION_DATA(":feature:authentication:data"),
    FEATURE_AUTHENTICATION_DOMAIN(":feature:authentication:domain"),
    FEATURE_AUTHENTICATION_PRESENTATION(":feature:authentication:presentation"),
    FEATURE_HOME_DATA(":feature:home:data"),
    FEATURE_HOME_DOMAIN(":feature:home:domain"),
    FEATURE_HOME_PRESENTATION(":feature:home:presentation"),
}