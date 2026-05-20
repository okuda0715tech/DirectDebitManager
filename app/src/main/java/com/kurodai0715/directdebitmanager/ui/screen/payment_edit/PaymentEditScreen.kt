package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.ReadOnlyForm
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

@Composable
fun PaymentEditScreen(
    paymentId: Int?,
    viewModel: PaymentEditViewModel,
    onClickBack: () -> Unit,
    onClickPayer: () -> Unit,
    onClickAddPayer: () -> Unit,
    onClickAddPayee: () -> Unit,
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

                    PaymentEditUiEvent.OnClickAddPayer -> onClickAddPayer()

                    PaymentEditUiEvent.OnClickAddPayee -> onClickAddPayee()
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
            payer = uiState.payer,
            onClickDetachPayer = { viewModel.onClickDetachPayer() },
            onClickAddPayer = { viewModel.onClickAddPayer() },
            payees = uiState.payees,
            onClickDetachPayee = { viewModel.onClickDetachPayee(it) },
            onClickAddPayee = { viewModel.onClickAddPayee() },
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
    payer: PaymentEditUiState.Payer,
    onClickDetachPayer: () -> Unit,
    onClickAddPayer: () -> Unit,
    payees: List<PaymentEditUiState.Payee>,
    onClickDetachPayee: (Int) -> Unit,
    onClickAddPayee: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(
                paymentName = paymentName,
                onPaymentNameChanged = onPaymentNameChanged,
                paymentNameMessage = paymentNameMessage,
                payer = payer,
                onClickDetachPayer = onClickDetachPayer,
                onClickAddPayer = onClickAddPayer,
                payees = payees,
                onClickDetachPayee = onClickDetachPayee,
                onClickAddPayee = onClickAddPayee
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
    payer: PaymentEditUiState.Payer,
    onClickDetachPayer: () -> Unit,
    onClickAddPayer: () -> Unit,
    payees: List<PaymentEditUiState.Payee>,
    onClickDetachPayee: (Int) -> Unit,
    onClickAddPayee: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Payment(paymentName, onPaymentNameChanged, paymentNameMessage)

        Spacer(modifier = Modifier.size(LayoutTokens.smallSectionSpacing))

        Payer(payer, onClickAddPayer, onClickDetachPayer)

        Spacer(modifier = Modifier.size(LayoutTokens.smallSectionSpacing))

        Payees(
            payees = payees,
            onClickDetachPayee = onClickDetachPayee,
            onClickAddPayee = onClickAddPayee
        )
    }
}

@Composable
private fun Payment(
    paymentName: String,
    onPaymentNameChanged: (String) -> Unit,
    paymentNameMessage: Int?
) {
    EditableForm(
        labelText = stringResource(R.string.payment_name_label),
        text = paymentName,
        onTextChanged = onPaymentNameChanged,
        supportingText = paymentNameMessage,
        onClickClear = { onPaymentNameChanged("") }
    )
}

@Composable
private fun Payer(
    payer: PaymentEditUiState.Payer,
    onClickAddPayer: () -> Unit,
    onClickDetachPayer: () -> Unit
) {
    when (payer) {
        is PaymentEditUiState.Payer.Unassigned -> {
            FilledTonalButton(onClick = { debouncedClick { onClickAddPayer() } }) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_link_24),
                    contentDescription = stringResource(R.string.add_payer_button_icon_description),
                    modifier = Modifier.size(ICON_LARGE_SIZE),
                )
                Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))
                Text(text = stringResource(R.string.add_payer))
            }

        }

        is PaymentEditUiState.Payer.Assigned -> {
            ReadOnlyForm(
                labelText = stringResource(R.string.payer_label),
                text = payer.name,
                onClickText = {},
                supportingText = payer.messageRes,
                icon = painterResource(id = R.drawable.outline_link_off_24),
                iconDescription = stringResource(id = R.string.detach_payee_icon_description),
                onClickIcon = onClickDetachPayer,
            )
        }
    }
}

@Composable
fun Payees(
    payees: List<PaymentEditUiState.Payee>,
    onClickDetachPayee: (Int) -> Unit,
    onClickAddPayee: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(LayoutTokens.itemSpacing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(text = stringResource(R.string.payee_label))

            Spacer(modifier = Modifier.size(LayoutTokens.itemSpacing))
        }

        itemsIndexed(payees) { index, payee ->
            Text(
                text = stringResource(R.string.individual_payee_label, index + 1),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = LayoutTokens.itemSpacing),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))

            ReadOnlyForm(
                labelText = stringResource(R.string.payee_name_label),
                text = payee.name,
                onClickText = {},
                supportingText = payee.messageRes,
                icon = painterResource(id = R.drawable.outline_link_off_24),
                iconDescription = stringResource(id = R.string.detach_payee_icon_description),
                onClickIcon = { onClickDetachPayee(payee.id) },
            )

            Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))

        }

        item {
            FilledTonalButton(onClick = { debouncedClick { onClickAddPayee() } }) {
                Icon(
                    painter = painterResource(R.drawable.outline_add_link_24),
                    contentDescription = stringResource(R.string.add_payee_button_icon_description),
                    modifier = Modifier.size(ICON_LARGE_SIZE),
                )
                Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))
                Text(text = stringResource(R.string.add_payee))
            }
        }
    }
}

@Preview(name = "TransferRelationEditContents")
@Composable
private fun Preview() {
    TransferRelationEditContents(
        onClickBack = {},
        onClickSave = {},
        paymentName = "リクルートカードプラス",
        onPaymentNameChanged = {},
        paymentNameMessage = null,
        payer = PaymentEditUiState.Payer.Assigned(
            id = 1,
            name = "三井住友銀行",
        ),
        onClickDetachPayer = {},
        onClickAddPayer = {},
        payees = listOf(
            PaymentEditUiState.Payee(id = 1, name = "電気料金"),
            PaymentEditUiState.Payee(id = 2, name = "水道料金"),
            PaymentEditUiState.Payee(id = 3, name = "ガス料金"),
        ),
        onClickDetachPayee = {},
        onClickAddPayee = {},
    )
}

@Preview(name = "TransferRelationEditContents")
@Composable
private fun NoPayerNoPayeePreview() {
    TransferRelationEditContents(
        onClickBack = {},
        onClickSave = {},
        paymentName = "リクルートカードプラス",
        onPaymentNameChanged = {},
        paymentNameMessage = null,
        payer = PaymentEditUiState.Payer.Unassigned,
        onClickDetachPayer = {},
        onClickAddPayer = {},
        payees = listOf(),
        onClickDetachPayee = {},
        onClickAddPayee = {},
    )
}
