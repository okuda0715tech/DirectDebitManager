package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
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
        supportingTextRes = uiState.paymentNameErrorMessage,
    )
}

@Composable
fun TransferRelationEditContents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    paymentName: String,
    onPaymentNameChanged: (String) -> Unit,
    supportingTextRes: Int?
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(
                paymentName = paymentName,
                onPaymentNameChanged = onPaymentNameChanged,
                supportingTextRes = supportingTextRes,
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
    supportingTextRes: Int?
) {
    Column {
        EditableForm(
            labelText = stringResource(R.string.payment_name_label),
            text = paymentName,
            onTextChanged = onPaymentNameChanged,
            supportingText = supportingTextRes,
            onClickClear = { onPaymentNameChanged("") }
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
        supportingTextRes = null,
    )
}