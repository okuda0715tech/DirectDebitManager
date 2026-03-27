package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.ReadOnlyForm
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentInfoEditScreen(
    viewModel: PaymentInfoEditViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TransferRelationEditContents(
        onClickBack = onClickBack,
        paymentName = uiState.paymentName,
        onPaymentNameChanged = { viewModel.updatePaymentName(it) },
        paymentNameMessage = uiState.paymentNameMessage,
        payerName = uiState.payerName,
        onClickPayer = { /* TODO */ },
        payerNameMessage = uiState.payerNameMessage,
    )
}

@Composable
fun TransferRelationEditContents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    paymentName: String,
    onPaymentNameChanged: (String) -> Unit,
    paymentNameMessage: Int?,
    payerName: String,
    onClickPayer: () -> Unit,
    payerNameMessage: Int?,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(
                paymentName = paymentName,
                onPaymentNameChanged = onPaymentNameChanged,
                paymentNameMessage = paymentNameMessage,
                payerName = payerName,
                onClickPayer = onClickPayer,
                payerNameMessage = payerNameMessage,
            )
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick(TODO()) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_save)
            )
        }
    )

}

@Composable
fun Contents(
    paymentName: String,
    onPaymentNameChanged: (String) -> Unit,
    paymentNameMessage: Int?,
    payerName: String,
    onClickPayer: () -> Unit,
    payerNameMessage: Int?,
) {
    Column {
        EditableForm(
            labelText = stringResource(R.string.payment_name_label),
            text = paymentName,
            onTextChanged = onPaymentNameChanged,
            supportingText = paymentNameMessage,
            onClickClear = { onPaymentNameChanged("") }
        )

        ReadOnlyForm(
            labelText = stringResource(R.string.payer_name_label),
            text = payerName,
            onClickText = onClickPayer,
            supportingText = payerNameMessage,
            icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
            iconDescription = stringResource(id = R.string.open_payment_list_screen_icon_description),
            onClickIcon = onClickPayer,
        )
    }
}

@Preview(name = "TransferRelationEditContents")
@Composable
private fun Preview() {
    TransferRelationEditContents(
        onClickBack = {},
        paymentName = "三井住友銀行",
        onPaymentNameChanged = {},
        paymentNameMessage = null,
        payerName = "",
        onClickPayer = {},
        payerNameMessage = null,
    )
}