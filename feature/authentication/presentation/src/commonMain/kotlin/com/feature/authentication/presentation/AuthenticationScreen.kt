package com.feature.authentication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.feature.authentication.presentation.social_media.ui.SocialMediaBlockContent
import com.feature.authentication.presentation.view_data.AuthenticationViewData
import com.store.core.presentation.theme.PreviewTheme
import com.store.core.presentation.theme.StoreTheme
import com.store.core.presentation.utils.ViewAction
import com.store.core.utils.AdaptivePreview
import com.store.core.utils.Alpha
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = koinViewModel(),
    navigateToHome: () -> Unit
) {
    val viewData by viewModel.viewData.collectAsState()
    AuthenticationContent(viewData) {}
}

@Composable
fun AuthenticationContent(
    viewData: AuthenticationViewData,
    onViewAction: (ViewAction) -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(all = 24.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "NUTRISPORT",
                    textAlign = TextAlign.Center,
                    style = StoreTheme.typography.bxl,
                    color = StoreTheme.color.textSecondary
                )
                Text(
                    modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                    text = "Sign in to continue",
                    textAlign = TextAlign.Center,
                    style = StoreTheme.typography.rl,
                    color = StoreTheme.color.textPrimary
                )
            }
            SocialMediaBlockContent(
                viewData = viewData.socialMedia, onViewAction = onViewAction
            )
        }
    }
}

@AdaptivePreview
@Composable
fun AuthenticationScreenPreview() {
    PreviewTheme {
        AuthenticationContent(AuthenticationMockPreview.getViewData()) {}
    }
}