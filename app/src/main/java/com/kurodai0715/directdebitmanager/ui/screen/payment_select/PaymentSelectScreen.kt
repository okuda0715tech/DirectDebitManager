package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultListItemFrame

@Composable
fun PaymentSelectScreen(
    viewModel: PaymentSelectViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentSelectContents(
        payments = uiState.payments,
        onClickItem = { viewModel.onClickItem(it) }
    )
}

@Composable
fun PaymentSelectContents(
    payments: List<Payment>,
    onClickItem: (Payment) -> Unit,
) {
    Contents(payments, onClickItem)
}

@Composable
private fun Contents(
    payments: List<Payment>,
    onClickItem: (Payment) -> Unit
) {
    LazyColumn {
        items(payments) { item ->
            ListItem(item, onClickItem = { onClickItem(item) })
        }
    }
}

@Composable
fun ListItem(
    item: Payment,
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
            Payment("三井住友銀行", true),
            Payment("リクルートカードプラス", false),
            Payment("横浜銀行", false),
            Payment("楽天銀行", false),
            Payment("電気料金", false),
        ),
        onClickItem = { }
    )
}