package org.cmp.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.store.core.presentation.theme.BaseTheme
import org.cmp.store.presentation.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
//        WindowCompat.enableEdgeToEdge(window)
        super.onCreate(savedInstanceState)
        setContent {
            BaseTheme { App() }
        }
    }
}