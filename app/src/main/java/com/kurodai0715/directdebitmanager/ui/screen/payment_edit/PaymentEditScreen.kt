package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.launch

@Composable
fun PaymentEditScreen(
    paymentId: Int?,
    viewModel: PaymentEditViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
    onClickPayer: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    Scaffold(snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState,
            // Snackbar がキーボードで隠れないようにする。
            modifier = Modifier.safeDrawingPadding()
        )
    }) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is PaymentEditUiEvent.ShowSnackbar -> launch {
                        // showSnackbar() 関数は suspend 関数であるため、スナックバーが消えるまで
                        // 次の命令に進めない。そのため、 launch{} ブロック内で実行することにより、
                        // 別の子ルーチン化することにより、すぐに後続のコルーチンを開始している。
                        snackbarHostState.showSnackbar(
                            message = context.getString(event.messageRes)
                        )
                    }

                    // TODO
                    // 画面遷移もイベントとして扱い、ここで実装する。

                }
            }
        }

        TransferRelationEditContents(
            modifier = Modifier.padding(paddingValues),
            onClickBack = onClickBack,
            onClickSave = { viewModel.save() },
            paymentName = uiState.paymentName,
            onPaymentNameChanged = { viewModel.updatePaymentName(it) },
            paymentNameMessage = uiState.paymentNameMessage,
            payerName = uiState.payerName,
            onClickPayer = onClickPayer,
            payerNameMessage = uiState.payerNameMessage,
        )
    }
}

@Composable
fun TransferRelationEditContents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
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
                onClickRight = { debouncedClick(onClickSave) },
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
            icon = painterResource(id = R.drawable.outline_arrow_right_24),
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
        onClickSave = {},
        paymentName = "三井住友銀行",
        onPaymentNameChanged = {},
        paymentNameMessage = null,
        payerName = "",
        onClickPayer = {},
        payerNameMessage = null,
    )
}