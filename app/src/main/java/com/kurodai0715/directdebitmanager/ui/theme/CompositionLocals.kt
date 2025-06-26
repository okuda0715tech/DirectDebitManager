package com.kurodai0715.directdebitmanager.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader

val LocalImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("ImageLoader not provided")
}
