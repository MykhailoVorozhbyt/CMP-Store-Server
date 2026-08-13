package com.feature.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.feature.home.presentation.utils.DrawerItem
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.AdaptivePreview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@TraceRecomposition(tag = "DrawerItemCard")
@Composable
fun DrawerItemCard(
    drawerItem: DrawerItem,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        selected = false,
        label = {
            Text(
                text = stringResource(drawerItem.title),
                textAlign = TextAlign.Center,
                style = StoreTheme.typography.rl,
                color = StoreTheme.color.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(drawerItem.icon),
                contentDescription = null,
                tint = StoreTheme.color.iconPrimary
            )
        }
    )
}


@AdaptivePreview
@Composable
private fun DrawerItemCardPreview() {
    PreviewTheme {
        DrawerItemCard(drawerItem = DrawerItem.Admin) {}
    }
}