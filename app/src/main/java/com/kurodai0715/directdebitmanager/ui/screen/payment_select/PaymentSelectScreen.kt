package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultListItemFrame
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.screen.payment_edit.PaymentEditViewModel
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentSelectScreen(
    sharedViewModel: PaymentEditViewModel,
    viewModel: PaymentSelectViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {

    val uiStateV2 by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentSelectContents(
        uiState = uiStateV2,
        onClickItem = { viewModel.onClickItem(it) },
        onClickBack = onClickBack,
    )
}

@Composable
fun PaymentSelectContents(
    modifier: Modifier = Modifier,
    uiState: PaymentSelectUiState,
    onClickItem: (PaymentSelectUiState.Success.Item) -> Unit,
    onClickBack: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(uiState, onClickItem)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick { TODO() } },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_select)
            )
        }
    )
}

@Composable
private fun Contents(
    uiState: PaymentSelectUiState,
    onClickItem: (PaymentSelectUiState.Success.Item) -> Unit
) {
    when (uiState) {
        is PaymentSelectUiState.Error -> {
            // エラー表示
            Text(text = stringResource(uiState.errorMessageRes))
        }

        PaymentSelectUiState.Loading -> {
            // ローディング表示
        }

        is PaymentSelectUiState.Success -> {
            LazyColumn {
                items(uiState.payments) { item ->
                    ListItem(
                        isSelected = uiState.isSelected(item.id),
                        item = item,
                        onClickItem = { onClickItem(item) })
                }
            }
        }
    }
}

@Composable
fun ListItem(
    isSelected: Boolean,
    item: PaymentSelectUiState.Success.Item,
    onClickItem: () -> Unit
) {
    DefaultListItemFrame(onClickItem = onClickItem) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.weight(1f), text = item.name)

            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                    contentDescription = stringResource(id = R.string.selected_icon_description),
                    modifier = Modifier
                        .size(ICON_LARGE_SIZE)
                        .clickable(onClick = { debouncedClick(onClickItem) }),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Preview(name = "PaymentSelectContents")
@Composable
private fun Preview() {
    PaymentSelectContents(
        uiState = PaymentSelectUiState.Success(
            payments = listOf(
                PaymentSelectUiState.Success.Item(1, "三井住友銀行"),
                PaymentSelectUiState.Success.Item(2, "リクルートカードプラス"),
                PaymentSelectUiState.Success.Item(3, "横浜銀行"),
                PaymentSelectUiState.Success.Item(4, "楽天銀行"),
                PaymentSelectUiState.Success.Item(5, "電気料金"),
            ),
        ),
        onClickItem = { },
        onClickBack = { },
    )
}