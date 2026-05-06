package com.feature.home.presentation.utils

import com.store.core.resources.Res
import com.store.core.resources.Resources
import com.store.core.resources.drawer_item_admin_panel
import com.store.core.resources.drawer_item_blog
import com.store.core.resources.drawer_item_contact_us
import com.store.core.resources.drawer_item_locations
import com.store.core.resources.drawer_item_profile
import com.store.core.resources.drawer_item_sign_out
import org.cmp.store.navigation.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class DrawerItem(
    val title: StringResource,
    val icon: DrawableResource,
    val navigation: Screen? = null,
    val default: Boolean = true
) {
    Profile(
        title = Res.string.drawer_item_profile,
        icon = Resources.Icon.Person,
        navigation = Screen.Profile
    ),
    Blog(
        title = Res.string.drawer_item_blog,
        icon = Resources.Icon.Book
    ),
    Locations(
        title = Res.string.drawer_item_locations,
        icon = Resources.Icon.MapPin,
    ),
    Contact(
        title = Res.string.drawer_item_contact_us,
        icon = Resources.Icon.Edit,
        navigation = Screen.ContactUs
    ),
    SignOut(
        title = Res.string.drawer_item_sign_out,
        icon = Resources.Icon.SignOut
    ),
    Admin(
        title = Res.string.drawer_item_admin_panel,
        icon = Resources.Icon.Unlock,
        navigation = Screen.AdminPanel,
        default = false
    )
}