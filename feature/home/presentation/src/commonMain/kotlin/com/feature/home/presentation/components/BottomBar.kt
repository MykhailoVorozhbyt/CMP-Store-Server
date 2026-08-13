package com.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.feature.home.presentation.HomeGraphMockPreview
import com.feature.home.presentation.utils.BottomBarDestination
import com.feature.home.presentation.view_data.CustomerViewData
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.RequestState
import com.store.core.presentation.utils.AdaptivePreview
import com.store.core.utils.Alpha
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    customer: RequestState<CustomerViewData>,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = StoreTheme.dimens.bottomBarShape))
            .background(StoreTheme.color.surfaceLight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomBarDestination.entries.forEach { destination ->
            BottomBarItem(
                destination = destination,
                selected = selected == destination,
                cartBadgeVisible = destination == BottomBarDestination.Cart
                        && customer.isSuccess()
                        && customer.successData().cart.isNotEmpty(),
                onSelect = onSelect,
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    destination: BottomBarDestination,
    selected: Boolean,
    cartBadgeVisible: Boolean,
    onSelect: (BottomBarDestination) -> Unit,
) {
    ShortNavigationBarItem(
        selected = selected,
        onClick = { onSelect(destination) },
        icon = {
            BadgedBox(
                badge = {
                    if (cartBadgeVisible) {
                        Badge(
                            modifier = Modifier
                                .size(8.dp)
                                .offset(x = 12.dp, y = (-12).dp)
                                .clip(CircleShape),
                            containerColor = StoreTheme.color.iconSecondary
                        )
                    }
                },
                content = {
                    Icon(
                        painter = painterResource(destination.icon),
                        contentDescription = null,
                    )
                }
            )
        },
        label = {
            Text(
                text = destination.title,
                style = StoreTheme.typography.bs,
                color = StoreTheme.color.textPrimary
            )
        },
        colors = ShortNavigationBarItemDefaults.colors(
            selectedIconColor = StoreTheme.color.iconSecondary,
            selectedTextColor = StoreTheme.color.iconSecondary,
            selectedIndicatorColor = StoreTheme.color.surfaceSecondary.copy(alpha = Alpha.TEN_PERCENT),
            unselectedIconColor = StoreTheme.color.iconPrimary,
            unselectedTextColor = StoreTheme.color.iconPrimary,
        )
    )
}

@AdaptivePreview
@Composable
private fun BottomBarPreview() {
    PreviewTheme {
        BottomBar(
            customer = RequestState.Success(HomeGraphMockPreview.getCustomer()),
            selected = BottomBarDestination.Cart,
        ) {}
    }
}