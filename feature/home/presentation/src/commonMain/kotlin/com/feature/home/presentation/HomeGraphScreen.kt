package com.feature.home.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.NavDisplay
import com.feature.home.presentation.components.BottomBar
import com.feature.home.presentation.components.CustomDrawer
import com.feature.home.presentation.utils.BottomBarDestination
import com.feature.home.presentation.utils.CustomDrawerState
import com.feature.home.presentation.utils.TOP_LEVEL_SCREENS
import com.feature.home.presentation.utils.isOpened
import com.feature.home.presentation.view_data.CustomerViewData
import com.feature.home.presentation.view_data.HomeGraphViewAction
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.store.core.navigation.LocalAppNavigator
import com.store.core.navigation.NavigationState
import com.store.core.navigation.RootNavigator
import com.store.core.navigation.rememberNavigationState
import com.store.core.navigation.toEntries
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.MessageEventData
import com.store.core.presentation.ui.base.UiEvent
import com.store.core.presentation.ui.base.collectEventsWithDefaultProcessing
import com.store.core.presentation.ui.components.StoreSnackbar
import com.store.core.presentation.ui.components.StoreSnackbarHostState
import com.store.core.presentation.utils.RequestState
import com.store.core.resources.Res
import com.store.core.resources.Resources
import com.store.core.resources.checkout_icon_description
import com.store.core.resources.close_menu_description
import com.store.core.resources.open_menu_description
import com.store.core.presentation.utils.AdaptivePreview
import com.store.core.utils.Alpha
import com.store.core.presentation.navigation.Screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGraphScreen(
    viewModel: HomeGraphViewModel = koinViewModel(),
    welcomeMessage: String? = null,
) {
    val navigationState = rememberNavigationState(
        startKey = Screen.ProductsOverview,
        topLevelKeys = TOP_LEVEL_SCREENS,
    )
    val rootNavigator = LocalAppNavigator.current
    val navigator = remember(navigationState) { RootNavigator(navigationState) }

    val selectedDestination by remember(navigationState.currentTopLevelKey) {
        derivedStateOf {
            BottomBarDestination.entries.firstOrNull {
                it.screen == navigationState.currentTopLevelKey
            } ?: BottomBarDestination.ProductsOverview
        }
    }

    val viewData by viewModel.viewDataState.collectAsState()

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

    // Stable lambdas
    val onDrawerToggle: () -> Unit = remember(rootNavigator) {
        { viewModel.onDrawerToggle() }
    }
    val onDrawerItemClick: (Screen) -> Unit = remember(rootNavigator) {
        { screen -> rootNavigator.navigate(screen) }
    }
    val onSignOutClick: () -> Unit = remember {
        { viewModel.onViewAction(HomeGraphViewAction.SignOutClicked) }
    }
    val onCheckoutClick: () -> Unit = remember {
        { viewModel.onViewAction(HomeGraphViewAction.CheckoutClicked) }
    }
    val onBottomBarSelect: (BottomBarDestination) -> Unit = remember(navigator) {
        { destination -> navigator.navigate(destination.screen) }
    }

    HomeGraphContent(
        customer = viewData.customer,
        drawerState = viewData.drawerState,
        selectedDestination = selectedDestination,
        navigationState = navigationState,
        snackBarState = snackBarState,
        onDrawerToggle = onDrawerToggle,
        onDrawerItemClick = onDrawerItemClick,
        onSignOutClick = onSignOutClick,
        onCheckoutClick = onCheckoutClick,
        onBottomBarSelect = onBottomBarSelect,
        goBack = navigator::goBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@TraceRecomposition
@Composable
private fun HomeGraphContent(
    customer: RequestState<CustomerViewData>,
    drawerState: CustomDrawerState,
    selectedDestination: BottomBarDestination,
    navigationState: NavigationState,
    snackBarState: StoreSnackbarHostState,
    onDrawerToggle: () -> Unit,
    onDrawerItemClick: (Screen) -> Unit,
    onSignOutClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    onBottomBarSelect: (BottomBarDestination) -> Unit,
    goBack: () -> Unit,
) {
    val customDrawerOffsetValue = StoreTheme.dimens.customDrawerOffsetValue

    val animatedBackground by animateColorAsState(
        targetValue = if (drawerState.isOpened()) StoreTheme.color.surfaceLight else StoreTheme.color.surface
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (drawerState.isOpened()) 0.9f else 1f
    )
    val animatedRadius by animateDpAsState(
        targetValue = if (drawerState.isOpened()) 20.dp else 0.dp
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackground)
            .systemBarsPadding()
    ) {
        val offsetValue = maxWidth / customDrawerOffsetValue
        val animatedOffset by animateDpAsState(
            targetValue = if (drawerState.isOpened()) offsetValue else 0.dp
        )

        CustomDrawer(
            customer = customer,
            onItemClick = onDrawerItemClick,
            onSignOutClick = onSignOutClick,
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
            val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()
            Scaffold(
                containerColor = StoreTheme.color.surface,
                topBar = {
                    HomeTopBar(
                        selectedDestination = selectedDestination,
                        customer = customer,
                        drawerState = drawerState,
                        onDrawerToggle = onDrawerToggle,
                        onCheckoutClick = onCheckoutClick,
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    HomeGraphNavDisplay(navigationState, goBack, snackBarState)
                    BottomBar(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(all = 12.dp),
                        customer = customer,
                        selected = selectedDestination,
                        onSelect = onBottomBarSelect,
                    )
                }
            }
        }
    }
}

@TraceRecomposition
@Composable
private fun HomeGraphNavDisplay(
    navigationState: NavigationState,
    goBack: () -> Unit,
    snackBarState: StoreSnackbarHostState
) {
    val isPreview = LocalInspectionMode.current
    Box(modifier = Modifier.fillMaxSize()) {
        if (isPreview.not()) {
            NavDisplay(
                entries = navigationState.toEntries(),
                onBack = goBack,
            )
        }
        StoreSnackbar(snackBarState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
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
            containerColor = StoreTheme.color.surface,
            actionIconContentColor = StoreTheme.color.iconPrimary,
            navigationIconContentColor = StoreTheme.color.iconPrimary,
            titleContentColor = StoreTheme.color.textPrimary,
        )
    )
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

@AdaptivePreview
@Composable
private fun HomeGraphContentDrawerOpenedPreview() {
    PreviewTheme {
        HomeGraphContent(
            customer = RequestState.Success(HomeGraphMockPreview.getCustomer()),
            drawerState = CustomDrawerState.Opened,
            selectedDestination = BottomBarDestination.ProductsOverview,
            navigationState = HomeGraphMockPreview.getNavigationState(),
            snackBarState = StoreSnackbarHostState(),
            onDrawerToggle = {},
            onDrawerItemClick = {},
            onSignOutClick = {},
            onCheckoutClick = {},
            onBottomBarSelect = {},
            goBack = {},
        )
    }
}
