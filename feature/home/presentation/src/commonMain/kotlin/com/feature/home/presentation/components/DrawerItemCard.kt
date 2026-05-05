package com.feature.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.feature.home.presentation.HomeMockPreview
import com.feature.home.presentation.utils.BottomBarDestination
import com.feature.home.presentation.utils.DrawerItem
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.RequestState
import com.store.core.utils.AdaptivePreview
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun DrawerItemCard(
    drawerItem: DrawerItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = StoreTheme.dimens.buttonRoundedFull))
            .clickable { onClick() }
            .padding(StoreTheme.dimens.drawerItemPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(StoreTheme.dimens.spaceBetweenItems)
    ) {
        Icon(
            painter = painterResource(drawerItem.icon),
            contentDescription = "Drawer item icon",
            tint = StoreTheme.color.iconPrimary
        )
        Text(
            text = drawerItem.title,
            textAlign = TextAlign.Center,
            style = StoreTheme.typography.rl,
            color = StoreTheme.color.textPrimary
        )
    }
}


@AdaptivePreview
@Composable
private fun DrawerItemCardPreview() {
    PreviewTheme {
        DrawerItemCard(drawerItem = DrawerItem.Admin) {}
    }
}