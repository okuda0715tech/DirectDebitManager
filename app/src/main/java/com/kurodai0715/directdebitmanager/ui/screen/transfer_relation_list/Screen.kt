package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "TransferRelationListScreen"

@Composable
fun Screen(
    viewModel: TransferRelationListViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
    onClickAdd: () -> Unit,
    onClickItem: (Int) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Contents(
        onClickBack = onClickBack,
        onClickAdd = onClickAdd,
        onClickItem = onClickItem,
        uiState = uiState
    )
}

@Composable
fun Contents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAdd: () -> Unit,
    onClickItem: (Int) -> Unit,
    uiState: TransferRelationListUiState,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        body = {
            Body(onClickItem, uiState)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick(onClickAdd) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_add)
            )
        }
    )
}

@Composable
fun Body(
    onClickItem: (Int) -> Unit,
    uiState: TransferRelationListUiState,
) {
    when (uiState) {
        is TransferRelationListUiState.Loading -> {
            // ローディング表示
        }

        is TransferRelationListUiState.Error -> {
            // エラー表示
            Text(text = stringResource(uiState.errorMessageRes))
        }

        is TransferRelationListUiState.Success -> {
            LazyColumn {
                items(uiState.payments) { item ->
                    ListItem(item, onClickItem = { onClickItem(item.id) })
                }
            }
        }
    }
}

@Composable
fun ListItem(
    item: FlattenedTreeItem,
    onClickItem: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = {
                Log.v(TAG, "list item is clicked.")
                debouncedClick(onClickItem)
            })
            .padding(LayoutTokens.elementSpacing)
    ) {
        Text(text = item.prefixedLabel)
    }
}


@Preview
@Composable
private fun Preview() {
    Contents(
        onClickBack = { },
        onClickAdd = { },
        onClickItem = { },
        uiState = TransferRelationListUiState.Success(
            payments = listOf(
                FlattenedTreeItem(1, "テスト1", depth = Depth(1)),
                FlattenedTreeItem(2, "テスト2", depth = Depth(2)),
                FlattenedTreeItem(3, "テスト3", depth = Depth(3)),
                FlattenedTreeItem(4, "テスト4", depth = Depth(3)),
                FlattenedTreeItem(5, "テスト5", depth = Depth(1)),
            )
        )
    )
}