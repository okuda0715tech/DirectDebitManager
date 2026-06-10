package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.OneButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.BodyBottomButtonLayout
import com.kurodai0715.directdebitmanager.ui.theme.ICON_EX_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "TransferRelationListScreen"

@Composable
fun Screen(
    viewModel: ViewModel = hiltViewModel(),
    openPaymentEdit: () -> Unit,
    onClickItem: (Int) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Contents(
        openPaymentEdit = openPaymentEdit,
        onClickItem = onClickItem,
        uiState = uiState
    )
}

@Composable
fun Contents(
    modifier: Modifier = Modifier,
    openPaymentEdit: () -> Unit,
    onClickItem: (Int) -> Unit,
    uiState: UiState,
) {
    BodyBottomButtonLayout(
        modifier = modifier,
        body = {
            Body(
                onClickEmptyMessage = openPaymentEdit,
                onClickItem = onClickItem,
                uiState = uiState,
            )
        },
        bottomButton = {
            OneButton(
                onClick = { debouncedClick(openPaymentEdit) },
                text = stringResource(R.string.common_add)
            )
        }
    )
}

@Composable
fun Body(
    onClickEmptyMessage: () -> Unit,
    onClickItem: (Int) -> Unit,
    uiState: UiState,
) {
    when (uiState) {
        is UiState.Loading -> {
            // ローディング表示
        }

        is UiState.Error -> {
            // エラー表示
            Text(text = stringResource(uiState.errorMessageRes))
        }

        is UiState.Success -> {
            val payments = uiState.payments
            if (payments.isEmpty()) {
                EmptyView(onClickEmptyMessage)
            } else {
                LazyColumn {
                    items(uiState.payments) { item ->
                        ListItem(item, onClickItem = { onClickItem(item.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyView(onClickEmptyMessage: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(true) {
                debouncedClick(onClickEmptyMessage)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_wand_stars_24),
            contentDescription = stringResource(R.string.empty_icon_description),
            modifier = Modifier.size(ICON_EX_LARGE_SIZE),
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.size(LayoutTokens.sectionSpacing))

        Text(text = stringResource(R.string.empty_message_label))
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
        openPaymentEdit = { },
        onClickItem = { },
        uiState = UiState.Success(
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