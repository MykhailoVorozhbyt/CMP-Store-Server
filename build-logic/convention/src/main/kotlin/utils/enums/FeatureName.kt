package utils.enums

enum class FeatureName(val dirName: String, val components: List<ComponentName>) {
    AUTHENTICATION(
        dirName = "authentication",
        components = listOf(ComponentName.AUTH),
    ),
    HOME(
        dirName = "home",
        components = listOf(ComponentName.AUTH, ComponentName.CUSTOMER, ComponentName.PRODUCT),
    );

    companion object {
        fun fromDirName(dirName: String): FeatureName? = entries.firstOrNull { it.dirName == dirName }
    }
}
