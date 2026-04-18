package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
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
    viewModel: PaymentEditViewModel,
    onClickBack: () -> Unit,
    onClickPayer: () -> Unit,
    onClickPayee: () -> Unit,
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

        // リスト画面から引き継いだパラメータで UI 状態を初期化する。
        LaunchedEffect(paymentId) {
            viewModel.initialize(paymentId)
        }

        // 画面遷移もイベントに含める理由は、 ViewModel を経由したいため。
        // ViewModel を経由することで、今後、画面遷移前になんらかの処理が必要になった場合も対応が可能になる。
        // 例えば、 ViewModel 側でバリデーションチェックを行って、 OK の場合のみ画面遷移させる等。
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

                    PaymentEditUiEvent.OnClickBack -> onClickBack()

                    PaymentEditUiEvent.OnClickPayer -> onClickPayer()

                    PaymentEditUiEvent.OnClickPayee -> onClickPayee()
                }
            }
        }

        TransferRelationEditContents(
            modifier = Modifier.padding(paddingValues),
            onClickBack = { viewModel.onClickBack() },
            onClickSave = { viewModel.save() },
            paymentName = uiState.payment.name,
            onPaymentNameChanged = { viewModel.updatePaymentName(it) },
            paymentNameMessage = uiState.payment.messageRes,
            payerName = uiState.payer.name,
            onClickPayer = { viewModel.onClickPayer() },
            payerNameMessage = uiState.payer.messageRes,
            payees = uiState.payees,
            onClickPayee = { TODO() }
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
    payees: List<PaymentEditUiState.Payee>,
    onClickPayee: (Int) -> Unit,
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
                payees = payees,
                onClickPayee = onClickPayee
            )
        },
        bottomButton = {
            BottomButton(onClickBack, onClickSave)
        }
    )

}

@Composable
private fun BottomButton(onClickBack: () -> Unit, onClickSave: () -> Unit) {
    HorizontalTwoButton(
        onClickLeft = { debouncedClick(onClickBack) },
        onClickRight = { debouncedClick(onClickSave) },
        leftText = stringResource(R.string.common_back),
        rightText = stringResource(R.string.common_save)
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
    payees: List<PaymentEditUiState.Payee>,
    onClickPayee: (Int) -> Unit,
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

        Payees(payees = payees, onClickPayee = onClickPayee)
    }
}

@Composable
fun Payees(
    payees: List<PaymentEditUiState.Payee>,
    onClickPayee: (Int) -> Unit
) {
    LazyColumn {
        items(payees.size) { index ->
            ReadOnlyForm(
                labelText = stringResource(R.string.payee_name_label, index + 1),
                text = payees[index].name,
                onClickText = { onClickPayee(index) },
                supportingText = payees[index].messageRes,
                icon = painterResource(id = R.drawable.outline_arrow_right_24),
                iconDescription = stringResource(id = R.string.open_payment_list_screen_icon_description),
                onClickIcon = { onClickPayee(index) },
            )
        }
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
        payees = listOf(
            PaymentEditUiState.Payee(name = "テスト支払先1"),
            PaymentEditUiState.Payee(name = "テスト支払先2"),
            PaymentEditUiState.Payee(name = "テスト支払先3"),
        ),
        onClickPayee = {}
    )
}