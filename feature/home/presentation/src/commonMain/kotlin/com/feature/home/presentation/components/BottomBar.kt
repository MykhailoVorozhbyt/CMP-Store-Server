package com.feature.home.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.feature.home.presentation.HomeMockPreview
import com.feature.home.presentation.utils.BottomBarDestination
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.RequestState
import com.store.core.utils.AdaptivePreview
import com.store.core.utils.Alpha
import org.cmp.store.domain.customer.Customer
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun BottomBar(
    modifier: Modifier = Modifier,
    customer: RequestState<Customer>,
    selected: BottomBarDestination,
    onSelect: (BottomBarDestination) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = StoreTheme.dimens.bottomBarShape))
            .background(StoreTheme.color.surfaceLight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BottomBarDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selected == destination,
                onClick = { onSelect(destination) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (destination == BottomBarDestination.Cart) {
                                if (customer.isSuccess() && customer.getSuccessData().cart.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .offset(x = 12.dp, y = (-12).dp)
                                            .clip(CircleShape),
                                        containerColor = StoreTheme.color.iconSecondary
                                    )
                                }
                            }
                        },
                        content = {
                            Icon(
                                painter = painterResource(destination.icon),
                                contentDescription = "Bottom Bar destination icon",
                            )
                        }
                    )
                },
                label = {
                    Text(
                        destination.title,
                        style = StoreTheme.typography.bs
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = StoreTheme.color.iconSecondary,
                    selectedTextColor = StoreTheme.color.iconSecondary,
                    indicatorColor = StoreTheme.color.surfaceSecondary.copy(alpha = Alpha.TEN_PERCENT),
                    unselectedIconColor = StoreTheme.color.iconPrimary,
                    unselectedTextColor = StoreTheme.color.iconPrimary,
                )
            )
        }
    }
}

@AdaptivePreview
@Composable
private fun BottomBarPreview() {
    PreviewTheme {
        BottomBar(
            customer = RequestState.Success(HomeMockPreview.getCustomer()),
            selected = BottomBarDestination.Cart,
        ) {}
    }
}