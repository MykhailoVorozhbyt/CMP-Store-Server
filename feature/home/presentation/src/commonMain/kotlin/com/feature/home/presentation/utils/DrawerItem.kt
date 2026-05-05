package com.feature.home.presentation.utils

import com.store.core.resources.Resources
import org.jetbrains.compose.resources.DrawableResource

internal enum class DrawerItem(
    val title: String,
    val icon: DrawableResource
) {
    Profile(
        title = "Profile",
        icon = Resources.Icon.Person
    ),
    Blog(
        title = "Blog",
        icon = Resources.Icon.Book
    ),
    Locations(
        title = "Locations",
        icon = Resources.Icon.MapPin
    ),
    Contact(
        title = "Contact us",
        icon = Resources.Icon.Edit
    ),
    SignOut(
        title = "Sign Out",
        icon = Resources.Icon.SignOut
    ),
    Admin(
        title = "Admin Panel",
        icon = Resources.Icon.Unlock
    )
}