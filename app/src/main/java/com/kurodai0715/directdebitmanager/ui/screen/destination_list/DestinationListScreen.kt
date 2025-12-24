/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.repeatCount
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.OneButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.theme.LocalImageLoader
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "DestinationListScreen.kt"

@Composable
fun DestinationListScreen(
    viewModel: DestinationListViewModel = hiltViewModel(),
    onClickDestEdit: (Int?) -> Unit,
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
                .padding(LayoutTokens.screenPadding),
            tabType = uiState.tabType,
            items = uiState.items,
            onChangeTab = { viewModel.updateTabType(it) },
            onNavigateToEdit = onClickDestEdit
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }

    }
}

@Composable
fun DestinationListContents(
    modifier: Modifier = Modifier,
    tabType: TabType,
    items: List<DestWithSourceUiModel>,
    onChangeTab: (TabType) -> Unit,
    onNavigateToEdit: (Int?) -> Unit
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(items, tabType, onChangeTab, onNavigateToEdit)
        },
        bottomButton = {
            OneButton(
                onClick = { debouncedClick { onNavigateToEdit(null) } },
                text = stringResource(R.string.common_add)
            )
        }
    )
}

@Composable
private fun Contents(
    items: List<DestWithSourceUiModel>,
    tabType: TabType,
    onChangeTab: (TabType) -> Unit,
    onNavigateToEdit: (Int?) -> Unit
) {
    Column {
        if (items.isEmpty()) {
            WelcomeAnimation(modifier = Modifier.weight(1f))
        } else {
            ViewChangeTab(selectedTab = tabType, onChangeTab = onChangeTab)

            if (tabType == TabType.ListView) {
                ListView(items, onNavigateToEdit)
            } else {
                // 要素の親子関係を解析し、深さ情報を算出しやすいツリー構造そのものを組み立てる。
                val rootItem = buildNestedTree(items)
                // 組み立てたツリー構造を、深さ付きで表示用に平坦化する。
                val flatTree = flattenTree(rootItem)
                // 画面に表示する必要のない加工時にのみ必要なデータはリストから除外する。
                val displayTree = flatTree.filterNot { it.destId == 0 }
                TreeView(displayTree)
            }
        }

    }
}

@Composable
private fun ColumnScope.ListView(
    items: List<DestWithSourceUiModel>,
    onNavigateToEdit: (Int?) -> Unit
) {
    LazyColumn(modifier = Modifier.weight(1f)) {
        val itemsHasSource = items.filter { it.sourceId != 0 }
        itemsIndexed(itemsHasSource) { index, item ->
            val itemModifier = when (index) {
                // 最初のアイテムは Top に 2 倍のパディング、 Bottom に通常のパディング
                0 -> Modifier.padding(top = 16.dp, bottom = 8.dp)
                // 最後のアイテムは Top に通常のパディング、 Bottom に 2 倍のパディング
                items.size - 1 -> Modifier.padding(top = 8.dp, bottom = 16.dp)
                // それ以外のアイテムは Top と Bottom にパディング
                else -> Modifier.padding(vertical = 8.dp)
            }
            DestinationItem(item, itemModifier, onClickItem = { onNavigateToEdit(item.destId) })
        }
    }
}

@Composable
private fun ColumnScope.TreeView(
    flatTree: List<FlattenedTreeItemUiModel>,
) {
    Box(Modifier.weight(1f)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                .padding(8.dp)
        ) {
            items(flatTree) { item ->
                Text("${"     ".repeat(item.depth - 1)}└ ${item.label}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.ViewChangeTab(selectedTab: TabType, onChangeTab: (TabType) -> Unit) {
    SecondaryTabRow(
        selectedTabIndex = TabType.entries.indexOf(selectedTab)
    ) {
        TabType.entries.forEach { tab ->
            Tab(
                selected = tab == selectedTab,
                onClick = { onChangeTab(tab) },
                text = { Text(tab.label()) }
            )
        }
    }
}

@Suppress("ComposableNaming")
@Composable
private fun TabType.label(): String {
    return when (this) {
        TabType.ListView -> stringResource(R.string.list_view_tab_label)
        TabType.TreeView -> stringResource(R.string.tree_view_tab_label)
    }
}

@Composable
fun WelcomeAnimation(modifier: Modifier) {
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
        modifier = modifier.fillMaxWidth(),
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

@Composable
fun DestinationItem(
    destWithSourceUiModel: DestWithSourceUiModel,
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
        Text(destWithSourceUiModel.sourceName)
        Text(stringResource(R.string.down_arrow))
        Text(destWithSourceUiModel.destName)
    }
}

@Preview(name = "DestinationListContents")
@Composable
private fun PreviewDestinationListListView() {
    DestinationListContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF),
        tabType = TabType.ListView,
        items = listOf(
            DestWithSourceUiModel(1, "横浜銀行クレジットカード", 1, "横浜銀行"),
            DestWithSourceUiModel(2, "Oliveクレジットカード", 2, "三井住友銀行"),
            DestWithSourceUiModel(3, "電気料金", 2, "三井住友銀行"),
            DestWithSourceUiModel(4, "水道料金", 3, "PayPay銀行"),
            DestWithSourceUiModel(5, "PayPayカード", 3, "PayPay銀行"),
        ),
        onChangeTab = { },
        onNavigateToEdit = { })
}

@Preview(name = "DestinationListContents")
@Composable
private fun PreviewDestinationListTreeView() {
    DestinationListContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF),
        tabType = TabType.TreeView,
        items = listOf(
            DestWithSourceUiModel(1, "横浜銀行", 0, ""),
            DestWithSourceUiModel(2, "横浜銀行クレジットカード", 1, "横浜銀行"),
            DestWithSourceUiModel(3, "Oliveクレジットカード", 5, "三井住友銀行"),
            DestWithSourceUiModel(4, "電気料金", 5, "三井住友銀行"),
            DestWithSourceUiModel(5, "三井住友銀行", 0, ""),
            DestWithSourceUiModel(6, "水道料金", 3, "Oliveクレジットカード"),
        ),
        onChangeTab = { },
        onNavigateToEdit = { })
}