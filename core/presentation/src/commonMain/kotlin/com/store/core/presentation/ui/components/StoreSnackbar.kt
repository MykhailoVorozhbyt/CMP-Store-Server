package com.store.core.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.store.core.presentation.core.NotificationType
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.ui.base.MessageEventData
import org.jetbrains.compose.resources.painterResource

@Composable
fun StoreSnackbar(
    state: StoreSnackbarHostState,
    snackbarHostModifier: Modifier = Modifier,
    snackbarBoxModifier: Modifier = Modifier.fillMaxSize(),
) {
    StoreSnackbar(
        snackBarHostState = state.snackBarHostState,
        notificationType = state.notificationType,
        snackbarHostModifier = snackbarHostModifier,
        snackbarBoxModifier = snackbarBoxModifier
    )
}

@Composable
fun StoreSnackbar(
    snackBarHostState: SnackbarHostState,
    notificationType: NotificationType,
    snackbarHostModifier: Modifier = Modifier,
    snackbarBoxModifier: Modifier = Modifier.fillMaxSize(),
) {
    Box(modifier = snackbarBoxModifier, contentAlignment = Alignment.TopCenter) {
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = snackbarHostModifier
        ) { data ->
            Row(
                modifier = Modifier
                    .background(color = StoreTheme.color.getColorByAttr(notificationType.bgColorAttr))
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 33.dp)
                    .padding(vertical = 8.5.dp)
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(notificationType.iconResource),
                    contentDescription = null,
                    tint = StoreTheme.color.getColorByAttr(notificationType.tintColorAttr)
                )
                Text(
                    text = data.visuals.message,
                    style = StoreTheme.typography.bold.copy(
                        fontSize = 12.sp,
                        color = StoreTheme.color.getColorByAttr(notificationType.tintColorAttr)
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }

}

@Stable
class StoreSnackbarHostState {
    val snackBarHostState: SnackbarHostState = SnackbarHostState()
    private val _notificationType = mutableStateOf(NotificationType.INFO)
    val notificationType: NotificationType get() = _notificationType.value

    suspend fun show(message: MessageEventData, duration: SnackbarDuration = SnackbarDuration.Short) {
        _notificationType.value = message.type
        snackBarHostState.showSnackbar(
            message = message.message,
            duration = duration
        )
    }

}