package com.feature.home.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.NavDisplay
import com.feature.home.presentation.components.BottomBar
import com.feature.home.presentation.components.CustomDrawer
import com.feature.home.presentation.utils.BottomBarDestination
import com.feature.home.presentation.utils.CustomDrawerState
import com.feature.home.presentation.utils.TOP_LEVEL_SCREENS
import com.feature.home.presentation.utils.isOpened
import com.feature.home.presentation.utils.opposite
import com.feature.home.presentation.view_data.HomeGraphViewAction
import com.store.core.navigation.AppNavigator
import com.store.core.navigation.RootNavigator
import com.store.core.navigation.rememberNavigationState
import com.store.core.navigation.toEntries
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.MessageEventData
import com.store.core.presentation.ui.base.UiEvent
import com.store.core.presentation.ui.base.collectEventsWithDefaultProcessing
import com.store.core.presentation.ui.components.StoreSnackbar
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import com.store.core.resources.Resources
import com.store.core.utils.Alpha
import org.cmp.store.navigation.Screen
import org.cmp.store.utils.getScreenWidth
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

//TODO: think if NavigationSuiteScaffold needed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen(
    rootNavigator: AppNavigator,
    viewModel: HomeGraphViewModel = koinViewModel(),
    welcomeMessage: String? = null,
) {
    val navigationState = rememberNavigationState(
        startKey = Screen.ProductsOverview,
        topLevelKeys = TOP_LEVEL_SCREENS,
    )
    val navigator = remember { RootNavigator() }
    navigator.state = navigationState

    val selectedDestination by remember(navigationState.currentTopLevelKey) {
        derivedStateOf {
            BottomBarDestination.entries.firstOrNull {
                it.screen == navigationState.currentTopLevelKey
            } ?: BottomBarDestination.ProductsOverview
        }
    }

    val screenWidth = remember { getScreenWidth() }
    var drawerState by remember { mutableStateOf(CustomDrawerState.Closed) }

    val offsetValue by remember { derivedStateOf { (screenWidth / 1.5).dp } }
    val animatedOffset by animateDpAsState(
        targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
    )

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) StoreTheme.color.surfaceLight else StoreTheme.color.surface
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )

    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    val viewData by viewModel.viewDataState.collectAsState()
    val customer = viewData.customer

    val snackBarState = remember { StoreSnackbarHostState() }
    viewModel.collectEventsWithDefaultProcessing(
        snackbarHostState = snackBarState,
        processCustom = { event, defaultProcess ->
            when (event) {
                is UiEvent.Navigate -> rootNavigator.navigate(event.screen)
                is UiEvent.NavigateInclusive -> rootNavigator.replaceAll(event.screen)
                event -> defaultProcess(event)
            }
        })

    LaunchedEffect(welcomeMessage) {
        welcomeMessage?.let { snackBarState.show(MessageEventData.success(it)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .systemBarsPadding()
    ) {
        CustomDrawer(
            customer = customer,
            onItemClick = { navigator -> rootNavigator.navigate(navigator) },
            onSignOutClick = { viewModel.onViewAction(HomeGraphViewAction.SignOutClicked) },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(size = animatedRadius))
                .offset(x = animatedOffset)
                .scale(scale = animatedScale)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(size = animatedRadius),
                    ambientColor = Color.Black.copy(alpha = Alpha.DISABLED),
                    spotColor = Color.Black.copy(alpha = Alpha.DISABLED),
                )
        ) {
            Scaffold(
                containerColor = StoreTheme.color.surface,
                topBar = {
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
                                    IconButton(
                                        onClick = {
                                            viewModel.onViewAction(HomeGraphViewAction.CheckoutClicked)
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.RightArrow),
                                            contentDescription = "Right icon",
                                            tint = StoreTheme.color.iconPrimary,
                                        )
                                    }
                                }
                            }
                        },
                        navigationIcon = {
                            AnimatedContent(targetState = drawerState) { drawer ->
                                if (drawer.isOpened()) {
                                    IconButton(onClick = { drawerState = drawerState.opposite() }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close icon",
                                            tint = StoreTheme.color.iconPrimary,
                                        )
                                    }
                                } else {
                                    IconButton(onClick = { drawerState = drawerState.opposite() }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Menu),
                                            contentDescription = "Menu icon",
                                            tint = StoreTheme.color.iconPrimary,
                                        )
                                    }
                                }
                            }
                        },
                    )
                },
                bottomBar = {
                    Box(modifier = Modifier.padding(all = 12.dp)) {
                        BottomBar(
                            customer = customer,
                            selected = selectedDestination,
                            onSelect = { destination -> navigator.navigate(destination.screen) },
                        )
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    NavDisplay(
//                        modifier = Modifier.weight(1f),
                        entries = navigator.state.toEntries(),
                        onBack = navigator::goBack,
                    )
                    StoreSnackbar(snackBarState)
                }
            }
        }
    }
}

@Composable
fun NavigationPlaceholderScreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StoreTheme.color.brand1)
            .padding(StoreTheme.dimens.defaultPadding),
    ) {
        Text(title)
    }
}
