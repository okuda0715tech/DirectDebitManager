package com.kurodai0715.directdebitmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import coil.ImageLoader
import com.kurodai0715.directdebitmanager.ui.app_base.AppBaseScreen
import com.kurodai0715.directdebitmanager.ui.theme.DirectDebitManagerTheme
import com.kurodai0715.directdebitmanager.ui.theme.LocalImageLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DirectDebitManagerTheme {
                AppProviders(imageLoader = imageLoader) {
                    AppBaseScreen()
                }
            }
        }
    }
}

@Composable
fun AppProviders(
    imageLoader: ImageLoader,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalImageLoader provides imageLoader,
        // 他の CompositionLocal も、ここに連続的に記述可能。
    ) {
        content()
    }
}
