/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import coil.ImageLoader
import com.kurodai0715.directdebitmanager.ui.app_base.AppBaseScreen
import com.kurodai0715.directdebitmanager.ui.theme.DirectDebitManagerTheme
import com.kurodai0715.directdebitmanager.ui.theme.LocalDensityMode
import com.kurodai0715.directdebitmanager.ui.theme.LocalImageLoader
import com.kurodai0715.directdebitmanager.ui.theme.toDensityMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity.kt"

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

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppProviders(
    imageLoader: ImageLoader,
    content: @Composable () -> Unit
) {
    val activity = LocalActivity.current ?: error("This must be called in Activity Context.")
    val windowSizeClass = calculateWindowSizeClass(activity)
    val densityMode = windowSizeClass.widthSizeClass.toDensityMode()

    Log.d(TAG, "densityMode: $densityMode")

    CompositionLocalProvider(
        LocalImageLoader provides imageLoader,
        LocalDensityMode provides densityMode,
        // 他の CompositionLocal も、ここに連続的に記述可能。
    ) {
        content()
    }
}
