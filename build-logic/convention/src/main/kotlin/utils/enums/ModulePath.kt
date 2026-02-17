package utils.enums

enum class ModulePath(val path: String) {
    COMPOSE_APP(":composeApp"),
    SERVER(":server"),
    SHARED(":shared"),
    DI(":di"),
    CORE_PRESENTATION(":core:presentation"),
    CORE_UTILS(":core:utils"),
    CORE_RESOURCES(":core:resources"),
}