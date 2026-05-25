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
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.BodyBottomButtonLayout
import com.kurodai0715.directdebitmanager.ui.navigation.NavContract
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit.TransferRelationEditViewModel
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentSelectScreen(
    selectTarget: NavContract.SelectTarget,
    sharedViewModel: TransferRelationEditViewModel,
    viewModel: PaymentSelectViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {

    val uiStateV2 by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentSelectContents(
        uiState = uiStateV2,
        onClickItem = { viewModel.onClickItem(it) },
        onClickBack = onClickBack,
        onClickSelect = {
            viewModel.onClickSelect()?.let {
                sharedViewModel.onPaymentSelected(selectTarget, it)
                onClickBack()
            }
        }
    )
}

@Composable
fun PaymentSelectContents(
    modifier: Modifier = Modifier,
    uiState: PaymentSelectUiState,
    onClickItem: (PaymentSelectUiState.Success.Item) -> Unit,
    onClickBack: () -> Unit,
    onClickSelect: () -> Unit,
) {
    BodyBottomButtonLayout(
        modifier = modifier,
        body = {
            Contents(uiState, onClickItem)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick(onClickSelect) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_select),
                rightEnabled = uiState.canSelect,
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
                    DefaultListItemFrame(
                        isSelected = uiState.isSelected(item.id),
                        label = item.name,
                        onClickItem = { onClickItem(item) }
                    )
                }
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
        onClickSelect = { },
    )
}