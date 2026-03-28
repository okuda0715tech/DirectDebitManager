package com.kurodai0715.directdebitmanager.ui.screen.payment_select

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
fun PaymentSelectScreen(
    viewModel: PaymentSelectViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentSelectContents(
        payments = uiState.payments,
        onClickItem = { viewModel.onClickItem(it) },
        onClickBack = onClickBack,
    )
}

@Composable
fun PaymentSelectContents(
    modifier: Modifier = Modifier,
    payments: List<PaymentSelectUiState.Item>,
    onClickItem: (PaymentSelectUiState.Item) -> Unit,
    onClickBack: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(payments, onClickItem)
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
    payments: List<PaymentSelectUiState.Item>,
    onClickItem: (PaymentSelectUiState.Item) -> Unit
) {
    LazyColumn {
        items(payments) { item ->
            ListItem(item, onClickItem = { onClickItem(item) })
        }
    }
}

@Composable
fun ListItem(
    item: PaymentSelectUiState.Item,
    onClickItem: () -> Unit
) {
    DefaultListItemFrame(onClickItem = onClickItem) {
        Text(text = item.name)
    }
}

@Preview(name = "PaymentSelectContents")
@Composable
private fun Preview() {
    PaymentSelectContents(
        payments = listOf(
            PaymentSelectUiState.Item("三井住友銀行", true),
            PaymentSelectUiState.Item("リクルートカードプラス", false),
            PaymentSelectUiState.Item("横浜銀行", false),
            PaymentSelectUiState.Item("楽天銀行", false),
            PaymentSelectUiState.Item("電気料金", false),
        ),
        onClickItem = { },
        onClickBack = { },
    )
}