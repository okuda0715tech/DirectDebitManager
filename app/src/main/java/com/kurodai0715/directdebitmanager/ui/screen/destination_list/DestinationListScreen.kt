package com.kurodai0715.directdebitmanager.ui.screen.destination_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.DestWithSource
import com.kurodai0715.directdebitmanager.ui.common_ui.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.common_ui.OneButton
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "DestinationListScreen.kt"

@Composable
fun DestinationListScreen(
    viewModel: DestinationListViewModel = hiltViewModel(),
    onNavigateToEdit: (DestWithSource?) -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState,
            // Snackbar がキーボードで隠れないようにする。
            modifier = Modifier.safeDrawingPadding()
        )
    }) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.snackbarMessageShown()
            }
        }

        DestinationListContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            items = uiState.items,
            onNavigateToEdit = onNavigateToEdit
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }

    }
}

@Composable
fun DestinationListContents(
    modifier: Modifier = Modifier,
    items: List<DestWithSource>,
    onNavigateToEdit: (DestWithSource?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (items.isEmpty()) {
            WelcomeAnimation(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(items) { index, item ->
                    val itemModifier = when (index) {
                        // 最初のアイテムは Bottom にのみパディング
                        0 -> Modifier.padding(bottom = 8.dp)
                        // 最後のアイテムは Top に通常のパディング、 Bottom に 2 倍のパディング
                        items.size - 1 -> Modifier.padding(top = 8.dp, bottom = 16.dp)
                        // それ以外のアイテムは Top と Bottom にパディング
                        else -> Modifier.padding(vertical = 8.dp)
                    }
                    DestinationItem(item, itemModifier, onClickItem = { onNavigateToEdit(item) })
                }
            }
        }

        HorizontalDivider()

        OneButton(
            onClick = { debouncedClick { onNavigateToEdit(null) } },
            text = stringResource(R.string.common_add)
        )
    }
}

@Composable
fun WelcomeAnimation(modifier: Modifier) {
    val context = LocalContext.current

    // 画像ローダーに GIF デコーダーを追加
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .build()

    // gifRequest の作成：ローカルリソース ID を使う
    val gifRequest = ImageRequest.Builder(context)
        .data(R.drawable.welcom_animation)
        .build()

    // UI に表示
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = gifRequest,
            contentDescription = stringResource(R.string.welcome_animation_description),
            imageLoader = imageLoader,
            modifier = Modifier.size(300.dp)
        )
    }
}

@Composable
fun DestinationItem(
    destWithSource: DestWithSource,
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = {
                Log.v(TAG, "list item is clicked.")
                debouncedClick(onClickItem)
            })
            .padding(8.dp)
    ) {
        Text(destWithSource.sourceName)
        Text(stringResource(R.string.down_arrow))
        Text(destWithSource.destName)
    }
}

@Preview
@Composable
private fun Preview() {
    DestinationListContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF),
        items = listOf(
            DestWithSource(1, "横浜銀行クレジットカード", 1, "横浜銀行"),
            DestWithSource(2, "Oliveクレジットカード", 2, "三井住友銀行"),
            DestWithSource(3, "電気料金", 2, "三井住友銀行"),
            DestWithSource(4, "水道料金", 3, "PayPay銀行"),
            DestWithSource(5, "PayPayカード", 3, "PayPay銀行"),
        ),
        onNavigateToEdit = { })
}