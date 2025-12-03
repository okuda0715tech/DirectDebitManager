package com.kurodai0715.directdebitmanager.ui.screen.source_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.screen.source_edit.getSourceTypeStringRes
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "SourceListScreen.kt"

@Composable
fun SourceListScreen(
    viewModel: SourceListViewModel = hiltViewModel(),
    onNavigateToEdit: (Source?) -> Unit,
    onNavigateUp: () -> Unit,
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

        SourceListContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            items = uiState.items,
            onNavigateToEdit = onNavigateToEdit,
            onNavigateUp = onNavigateUp,
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }
    }
}

// TODO UI はドメインモデルに依存するべきではないため、修正が必要。
//  UI が依存しても良いのは、 UI 状態のみで、それ以外の場合は、プリミティブ型でデータを扱う。
@Composable
fun SourceListContents(
    modifier: Modifier = Modifier,
    items: List<Source>,
    onNavigateToEdit: (Source?) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
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
                TransSourceItem(item, itemModifier, onClickItem = { onNavigateToEdit(item) })
            }
        }

        HorizontalDivider()

        HorizontalTwoButton(
            onClickLeft = { debouncedClick(onNavigateUp) },
            onClickRight = { debouncedClick { onNavigateToEdit(null) } },
            leftText = stringResource(R.string.common_back),
            rightText = stringResource(R.string.common_add)
        )
    }
}

@Composable
fun TransSourceItem(
    source: Source,
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
        Text(source.name)
        Text(
            text = stringResource(getSourceTypeStringRes(source.type)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SourceListContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(SCREEN_EDGE_PADDING_DEF),
        items = listOf(
            Source(1, "横浜銀行クレジットカード", 1, TransferItemType.CreditCard),
            Source(2, "横浜銀行", 0, TransferItemType.Bank),
            Source(3, "三菱UFJ銀行", 0, TransferItemType.Bank),
            Source(4, "横浜銀行デビットカード", 2, TransferItemType.DebitCard),
            Source(5, "PayPay", 3, TransferItemType.Others),
        ),
        onNavigateUp = { },
        onNavigateToEdit = { },
    )
}
