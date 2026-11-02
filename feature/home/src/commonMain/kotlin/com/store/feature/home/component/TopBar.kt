package com.store.feature.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.AdaptivePreview
import com.store.core.presentation.utils.RequestState
import com.store.core.resources.Res
import com.store.core.resources.Resources
import com.store.core.resources.checkout_icon_description
import com.store.core.resources.close_menu_description
import com.store.core.resources.open_menu_description
import com.store.feature.home.HomeGraphMockPreview
import com.store.feature.home.utils.BottomBarDestination
import com.store.feature.home.utils.CustomDrawerState
import com.store.feature.home.utils.isOpened
import com.store.feature.home.view_data.CustomerViewData
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    selectedDestination: BottomBarDestination,
    customer: RequestState<CustomerViewData>,
    drawerState: CustomDrawerState,
    onDrawerToggle: () -> Unit,
    onCheckoutClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            AnimatedContent(targetState = selectedDestination) { destination ->
                Text(
                    text = destination.title,
                    style = StoreTheme.typography.topAppBar
                )
            }
        },
        actions = {
            AnimatedVisibility(visible = selectedDestination == BottomBarDestination.Cart) {
                if (customer.isSuccess() && customer.successData().cart.isNotEmpty()) {
                    IconButton(onClick = onCheckoutClick) {
                        Icon(
                            painter = painterResource(Resources.Icon.RightArrow),
                            contentDescription = stringResource(Res.string.checkout_icon_description),
                        )
                    }
                }
            }
        },
        navigationIcon = {
            AnimatedContent(targetState = drawerState) { drawer ->
                val icon = if (drawer.isOpened()) Resources.Icon.Close else Resources.Icon.Menu
                IconButton(onClick = onDrawerToggle) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = stringResource(
                            if (drawer.isOpened()) Res.string.close_menu_description
                            else Res.string.open_menu_description
                        ),
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = StoreTheme.color.backgroundSoft,
            actionIconContentColor = StoreTheme.color.iconPrimary,
            navigationIconContentColor = StoreTheme.color.iconPrimary,
            titleContentColor = StoreTheme.color.textPrimary,
        )
    )
}


@AdaptivePreview
@Composable
private fun TopBarPreview() {
    PreviewTheme {
        TopBar(
            selectedDestination = BottomBarDestination.ProductsOverview,
            customer = RequestState.Success(HomeGraphMockPreview.getCustomer()),
            drawerState = CustomDrawerState.Closed,
            onDrawerToggle = {},
            onCheckoutClick = {},
        )
    }
}