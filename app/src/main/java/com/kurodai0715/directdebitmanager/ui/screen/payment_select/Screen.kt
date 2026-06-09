package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.BodyBottomButtonLayout
import com.kurodai0715.directdebitmanager.ui.dialog.SaveCompletionDialog
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun Screen(
    viewModel: ViewModel,
    onClickBack: () -> Unit,
    backToRelationEditScreen: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentSelectContents(
        uiState = uiState,
        onClickItem = { viewModel.onClickItem(it) },
        onClickBack = onClickBack,
        onClickSave = viewModel::onClickSave,
        onClickSaveDialogClose = viewModel::onClickSaveDialogClose,
    )

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                UiEvent.BackToRelationEditScreen -> backToRelationEditScreen()
            }
        }
    }
}

@Composable
fun PaymentSelectContents(
    modifier: Modifier = Modifier,
    uiState: PaymentSelectUiState,
    onClickItem: (PaymentSelectUiState.Success.Item) -> Unit,
    onClickBack: () -> Unit,
    onClickSave: (Int) -> Unit,
    onClickSaveDialogClose: () -> Unit,
) {
    BodyBottomButtonLayout(
        modifier = modifier,
        body = {
            Contents(uiState, onClickItem, onClickSaveDialogClose)
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = {
                    when (uiState) {
                        is PaymentSelectUiState.Success -> uiState.selectedId?.let {
                            debouncedClick { onClickSave(it) }
                        }

                        else -> {
                            // 何もしない
                        }
                    }
                },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_save),
                rightEnabled = uiState.saveButtonEnabled,
            )
        }
    )
}

@Composable
private fun Contents(
    uiState: PaymentSelectUiState,
    onClickItem: (PaymentSelectUiState.Success.Item) -> Unit,
    onClickSaveDialogClose: () -> Unit,
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
            Column {
                Spacer(modifier = Modifier.size(LayoutTokens.sectionSpacingHalf))

                Text(
                    text = stringResource(
                        uiState.explainMessageRes,
                        uiState.paymentName,
                    )
                )

                Spacer(modifier = Modifier.size(LayoutTokens.smallSectionSpacing))

                LazyColumn {
                    items(uiState.payments) { item ->
                        ListItemFrame(
                            itemState = item.state,
                            label = item.name,
                            onClickItem = { onClickItem(item) }
                        )
                    }
                }
            }

            when (uiState.dialog) {
                PaymentSelectUiState.Success.Dialog.SaveSuccess -> {
                    SaveCompletionDialog(onClickClose = onClickSaveDialogClose)
                }

                PaymentSelectUiState.Success.Dialog.SaveFailed -> {
                    TODO()
                }

                PaymentSelectUiState.Success.Dialog.None -> {
                    // 何も表示しない
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
            paymentName = "三菱UFJ銀行",
            explainMessageRes = R.string.payee_select_explain_label,
            selectedId = 5,
            payments = listOf(
                PaymentSelectUiState.Success.Item(1, "三井住友銀行", ItemState.None),
                PaymentSelectUiState.Success.Item(2, "リクルートカードプラス", ItemState.None),
                PaymentSelectUiState.Success.Item(3, "横浜銀行", ItemState.None),
                PaymentSelectUiState.Success.Item(4, "水道料金", ItemState.Registered),
                PaymentSelectUiState.Success.Item(5, "電気料金", ItemState.Selected),
            ),
        ),
        onClickItem = { },
        onClickBack = { },
        onClickSave = { },
        onClickSaveDialogClose = { },
    )
}