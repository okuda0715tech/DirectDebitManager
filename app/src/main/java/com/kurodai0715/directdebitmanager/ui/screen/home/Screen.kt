package com.kurodai0715.directdebitmanager.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.repeatCount
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.LocalImageLoader
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


@Composable
fun Screen(onClickScreen: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(true) {
                debouncedClick { onClickScreen() }
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WelcomeAnimation()

        Text(text = stringResource(R.string.tap_to_next_label))
    }
}

@Composable
fun WelcomeAnimation(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 画像ローダーに GIF デコーダーを追加
    val imageLoader = LocalImageLoader.current

    // gifRequest の作成：ローカルリソース ID を使う
    val gifRequest = remember {
        ImageRequest.Builder(context)
            .data(R.drawable.welcom_animation)
            .repeatCount(20)
            .build()
    }

    // UI に表示
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = gifRequest,
            contentDescription = stringResource(R.string.welcome_animation_description),
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxWidth()
        )
    }
}