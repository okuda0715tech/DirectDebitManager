/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.source_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.SourceUiModel
import com.kurodai0715.directdebitmanager.domain.model.ItemType
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.screen.source_edit.getSourceTypeStringRes
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "SourceListScreen.kt"

@Composable
fun SourceListScreen(
    viewModel: SourceListViewModel = hiltViewModel(),
    onClickSourceEdit: (Int?) -> Unit,
    onClickNavigateUp: () -> Unit,
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
                .padding(LayoutTokens.screenPaddingHalf),
            items = uiState.items,
            onNavigateToEdit = onClickSourceEdit,
            onNavigateUp = onClickNavigateUp,
        )

        if (uiState.isLoading) {
            AppUncertainCircularIndicator()
        }
    }
}

@Composable
fun SourceListContents(
    modifier: Modifier = Modifier,
    items: List<SourceUiModel>,
    onNavigateToEdit: (Int?) -> Unit,
    onNavigateUp: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(items, onNavigateToEdit)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onNavigateUp) },
                onClickRight = { debouncedClick { onNavigateToEdit(null) } },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_add)
            )
        }
    )
}

@Composable
private fun Contents(
    items: List<SourceUiModel>,
    onNavigateToEdit: (Int?) -> Unit
) {
    LazyColumn {
        itemsIndexed(items) { index, item ->
            val itemModifier = when (index) {
                // 最初のアイテムは Bottom にのみパディング
                0 -> Modifier.padding(bottom = LayoutTokens.itemSpacingHalf)
                // 最後のアイテムは Top に通常のパディング、 Bottom に 2 倍のパディング
                items.size - 1 -> Modifier.padding(top = LayoutTokens.itemSpacingHalf)
                // それ以外のアイテムは Top と Bottom にパディング
                else -> Modifier.padding(vertical = LayoutTokens.itemSpacingHalf)
            }
            TransSourceItem(item, itemModifier, onClickItem = { onNavigateToEdit(item.id) })
        }
    }
}

@Composable
fun TransSourceItem(
    item: SourceUiModel,
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
            .padding(LayoutTokens.elementSpacing)
    ) {
        Text(item.name)

        Spacer(modifier = Modifier.height(LayoutTokens.elementSpacing))

        Text(
            text = stringResource(getSourceTypeStringRes(item.type)),
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
            .padding(LayoutTokens.screenPaddingHalf),
        items = listOf(
            SourceUiModel(1, "横浜銀行クレジットカード", ItemType.CreditCard),
            SourceUiModel(2, "横浜銀行", ItemType.Bank),
            SourceUiModel(3, "三菱UFJ銀行", ItemType.Bank),
            SourceUiModel(4, "横浜銀行デビットカード", ItemType.DebitCard),
            SourceUiModel(5, "PayPay", ItemType.Others),
        ),
        onNavigateUp = { },
        onNavigateToEdit = { },
    )
}
