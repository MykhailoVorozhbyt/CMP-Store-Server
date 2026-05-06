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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.feature.home.presentation.HomeGraphMockPreview
import com.feature.home.presentation.utils.DrawerItem
import com.feature.home.presentation.view_data.HomeGraphUiEvent
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.UiEvent
import com.store.core.presentation.utils.RequestState
import com.store.core.resources.Res
import com.store.core.resources.drawer_healthy_lifestyle
import com.store.core.utils.AdaptivePreview
import org.cmp.store.domain.customer.Customer
import org.cmp.store.navigation.Screen
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomDrawer(
    customer: RequestState<Customer>,
    onItemClick: (Screen) -> Unit,
    onSignOutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.6f)
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
            DrawerItemCard(
                drawerItem = item,
                onClick = {
                    when (item) {
                        DrawerItem.Profile, DrawerItem.Contact -> item.navigation?.let { navigation ->
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
            if (customerState.isSuccess() && customerState.getSuccessData().isAdmin) {
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