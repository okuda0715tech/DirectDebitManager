package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultListItemFrame
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun TransferRelationListScreen(
    viewModel: TransferRelationListViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
    onClickAdd: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TransferRelationListContents(
        onClickBack = onClickBack,
        onClickAdd = onClickAdd,
        payments = uiState.payments,
        onClickItem = { TODO() }
    )
}

@Composable
fun TransferRelationListContents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAdd: () -> Unit,
    payments: List<TransferRelationListUiState.Item>,
    onClickItem: (TransferRelationListUiState.Item) -> Unit
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(payments, onClickItem)
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
fun Contents(
    payments: List<TransferRelationListUiState.Item>,
    onClickItem: (TransferRelationListUiState.Item) -> Unit
) {
    LazyColumn {
        items(payments) { item ->
            ListItem(item, onClickItem = { onClickItem(item) })
        }
    }
}

@Composable
fun ListItem(
    item: TransferRelationListUiState.Item,
    onClickItem: () -> Unit
) {
    DefaultListItemFrame(onClickItem = onClickItem) {
        Text(text = item.name)
    }
}


@Preview
@Composable
private fun Preview() {
    TransferRelationListContents(
        onClickBack = { },
        onClickAdd = { },
        payments = listOf(
            TransferRelationListUiState.Item("テスト1"),
            TransferRelationListUiState.Item("テスト2"),
            TransferRelationListUiState.Item("テスト3"),
            TransferRelationListUiState.Item("テスト4"),
            TransferRelationListUiState.Item("テスト5"),
            TransferRelationListUiState.Item("テスト6"),
        ),
        onClickItem = { }
    )
}