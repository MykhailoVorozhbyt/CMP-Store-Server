package com.feature.home.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.feature.home.presentation.HomeGraphMockPreview
import com.feature.home.presentation.utils.DrawerItem
import com.feature.home.presentation.view_data.CustomerViewData
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.RequestState
import com.store.core.resources.Res
import com.store.core.resources.drawer_healthy_lifestyle
import com.store.core.presentation.utils.AdaptivePreview
import com.store.core.presentation.navigation.Screen
import org.jetbrains.compose.resources.stringResource

@TraceRecomposition(tag = "CustomDrawer")
@Composable
fun CustomDrawer(
    customer: RequestState<CustomerViewData>,
    onItemClick: (Screen) -> Unit,
    onSignOutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(StoreTheme.dimens.customDrawerWidth)
            .padding(horizontal = StoreTheme.dimens.spaceBetweenItems)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(StoreTheme.strings.appName),
            textAlign = TextAlign.Center,
            style = StoreTheme.typography.bxl,
            color = StoreTheme.color.textSecondary
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(Res.string.drawer_healthy_lifestyle),
            textAlign = TextAlign.Center,
            style = StoreTheme.typography.rm,
            color = StoreTheme.color.textPrimary
        )
        Spacer(modifier = Modifier.height(50.dp))
        DrawerItem.entries.filter { it.default }.forEach { item ->
            val drawer = remember { item }
            DrawerItemCard(
                drawerItem = drawer,
                onClick = {
                    when (drawer) {
                        DrawerItem.Profile, DrawerItem.Contact -> drawer.navigation?.let { navigation ->
                            onItemClick(navigation)
                        }

                        DrawerItem.SignOut -> onSignOutClick()
                        else -> Unit
                    }
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        AnimatedContent(targetState = customer) { customerState ->
            if (customerState.isSuccess() && customerState.successData().isAdmin) {
                DrawerItemCard(
                    drawerItem = DrawerItem.Admin,
                    onClick = { onItemClick(Screen.AdminPanel) }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@AdaptivePreview
@Composable
private fun CustomDrawerPreview() {
    PreviewTheme {
        CustomDrawer(
            customer = RequestState.Success(HomeGraphMockPreview.getCustomer()),
            onItemClick = {},
            onSignOutClick = {},
        )
    }
}