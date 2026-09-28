package utils.enums

enum class ComponentName(val dirName: String) {
    CUSTOMER("customer"),
    PRODUCT("product"),
    AUTH("auth");

    companion object {
        fun fromDirName(dirName: String): ComponentName? = entries.firstOrNull { it.dirName == dirName }
    }
}
