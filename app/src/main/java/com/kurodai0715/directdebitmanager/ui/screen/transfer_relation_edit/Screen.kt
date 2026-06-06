package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.ReadOnlyForm
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.BodyBottomButtonLayout
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteConfirmDialog2
import com.kurodai0715.directdebitmanager.ui.dialog.DetachConfirmDialog
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit.UiState.Dialog
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

@Composable
fun Screen(
    viewModel: ViewModel,
    onClickBack: () -> Unit,
    openPaymentEdit: (Int) -> Unit,
    openPayerSelect: (Int, Int) -> Unit,
    openPayeeSelect: (Int, Int) -> Unit,
    onDeleted: () -> Unit,
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

        // 画面遷移もイベントに含める理由は、 ViewModel を経由したいため。
        // ViewModel を経由することで、今後、画面遷移前になんらかの処理が必要になった場合も対応が可能になる。
        // 例えば、 ViewModel 側でバリデーションチェックを行って、 OK の場合のみ画面遷移させる等。
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> launch {
                        // showSnackbar() 関数は suspend 関数であるため、スナックバーが消えるまで
                        // 次の命令に進めない。そのため、 launch{} ブロック内で実行することにより、
                        // 別の子ルーチン化することにより、すぐに後続のコルーチンを開始している。
                        snackbarHostState.showSnackbar(
                            message = context.getString(event.messageRes)
                        )
                    }

                    is UiEvent.OpenPaymentEdit -> openPaymentEdit(event.paymentId)

                    is UiEvent.OpenPayerSelect -> openPayerSelect(event.payerId, event.paymentId)

                    is UiEvent.OpenPayeeSelect -> openPayeeSelect(event.payerId, event.paymentId)

                    UiEvent.OnDeleted -> onDeleted()
                }
            }
        }

        Contents(
            modifier = Modifier.padding(paddingValues),
            paymentName = uiState.payment.name,
            paymentNameMessage = uiState.payment.messageRes,
            payer = uiState.payer,
            payees = uiState.payees,
            onClickBack = onClickBack,
            onClickSave = { viewModel.save() },
            onClickDelete = { viewModel.onClickDeletePayment() },
            onClickPayment = { viewModel.onClickPayment() },
            onClickAddPayer = { viewModel.onClickAddPayer() },
            onClickAddPayee = { viewModel.onClickAddPayee() },
            onClickDetachPayerIcon = viewModel::onClickDetachPayerIcon,
            onClickDetachPayeeIcon = viewModel::onClickDetachPayeeIcon,
            onClickRelatedName = viewModel::onClickRelatedName,
        )

        DialogHost(
            dialog = uiState.dialog,
            onAction = viewModel::onDialogAction,
        )

    }
}

@Composable
fun DialogHost(
    dialog: Dialog,
    onAction: (Dialog.Action) -> Unit,
) {
    when (dialog) {
        is Dialog.DeleteConfirm -> {
            DeleteConfirmDialog2(
                itemName = dialog.itemName,
                onDismissRequest = { onAction(Dialog.Action.Dismiss) },
                onClickNo = { onAction(Dialog.Action.No) },
                onClickYes = { onAction(Dialog.Action.DeleteYes) },
            )
        }

        is Dialog.DetachPayerConfirm -> {
            DetachConfirmDialog(
                paymentName = dialog.paymentName,
                payerName = dialog.payerName,
                onDismissRequest = { onAction(Dialog.Action.Dismiss) },
                onClickNo = { onAction(Dialog.Action.No) },
                onClickYes = { onAction(Dialog.Action.DetachPayerYes) },
            )
        }

        is Dialog.DetachPayeeConfirm -> {
            DetachConfirmDialog(
                paymentName = dialog.paymentName,
                payerName = dialog.payeeName,
                onDismissRequest = { onAction(Dialog.Action.Dismiss) },
                onClickNo = { onAction(Dialog.Action.No) },
                onClickYes = { onAction(Dialog.Action.DetachPayeeYes(dialog.payeeId)) },
            )
        }

        Dialog.None -> {
            /* ダイアログを表示しない */
        }

    }
}

@Composable
fun Contents(
    modifier: Modifier = Modifier,
    paymentName: String,
    paymentNameMessage: Int?,
    payer: UiState.Payer,
    payees: List<UiState.Payee>,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
    onClickDelete: () -> Unit,
    onClickPayment: () -> Unit,
    onClickAddPayer: () -> Unit,
    onClickAddPayee: () -> Unit,
    onClickDetachPayerIcon: (String) -> Unit,
    onClickDetachPayeeIcon: (Int, String) -> Unit,
    onClickRelatedName: (Int) -> Unit,
) {
    BodyBottomButtonLayout(
        modifier = modifier,
        body = {
            Body(
                paymentName = paymentName,
                paymentNameMessage = paymentNameMessage,
                payer = payer,
                payees = payees,
                onClickPayment = onClickPayment,
                onClickDelete = onClickDelete,
                onClickAddPayer = onClickAddPayer,
                onClickAddPayee = onClickAddPayee,
                onClickDetachPayer = onClickDetachPayerIcon,
                onClickDetachPayee = onClickDetachPayeeIcon,
                onClickRelatedName = onClickRelatedName,
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
fun Body(
    paymentName: String,
    paymentNameMessage: Int?,
    payer: UiState.Payer,
    payees: List<UiState.Payee>,
    onClickPayment: () -> Unit,
    onClickDelete: () -> Unit,
    onClickAddPayer: () -> Unit,
    onClickAddPayee: () -> Unit,
    onClickDetachPayer: (String) -> Unit,
    onClickDetachPayee: (Int, String) -> Unit,
    onClickRelatedName: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Payment(
            paymentName = paymentName,
            paymentNameMessage = paymentNameMessage,
            onClickPayment = onClickPayment,
            onClickDelete = onClickDelete
        )

        Spacer(modifier = Modifier.size(LayoutTokens.smallSectionSpacingHalf))

        HorizontalDivider()

        Spacer(modifier = Modifier.size(LayoutTokens.smallSectionSpacingHalf))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Payer(
                    payer = payer,
                    onClickAddPayer = onClickAddPayer,
                    onClickDetachPayer = onClickDetachPayer,
                    onClickPayerName = onClickRelatedName
                )
            }

            item {
                Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))
            }

            payees(
                payees = payees,
                onClickDetachPayee = onClickDetachPayee,
                onClickAddPayee = onClickAddPayee,
                onClickPayeeName = onClickRelatedName,
            )
        }


    }
}

@Composable
private fun Payment(
    paymentName: String,
    paymentNameMessage: Int?,
    onClickPayment: () -> Unit,
    onClickDelete: () -> Unit,
) {
    ReadOnlyForm(
        labelText = stringResource(R.string.payment_name_label),
        text = paymentName,
        onClickText = onClickPayment,
        supportingText = paymentNameMessage,
        icon = painterResource(id = R.drawable.baseline_delete_outline_24),
        iconDescription = stringResource(id = R.string.delete_payment_icon_description),
        onClickIcon = { onClickDelete() }
    )
}

@Composable
private fun Payer(
    payer: UiState.Payer,
    onClickAddPayer: () -> Unit,
    onClickDetachPayer: (String) -> Unit,
    onClickPayerName: (Int) -> Unit,
) {
    when (payer) {
        is UiState.Payer.Unassigned -> {
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

        is UiState.Payer.Assigned -> {
            ReadOnlyForm(
                labelText = stringResource(R.string.payer_label),
                text = payer.name,
                onClickText = { onClickPayerName(payer.id) },
                supportingText = payer.messageRes,
                icon = painterResource(id = R.drawable.outline_link_off_24),
                iconDescription = stringResource(id = R.string.detach_payee_icon_description),
                onClickIcon = { onClickDetachPayer(payer.name) },
            )
        }
    }
}

fun LazyListScope.payees(
    payees: List<UiState.Payee>,
    onClickDetachPayee: (Int, String) -> Unit,
    onClickAddPayee: () -> Unit,
    onClickPayeeName: (Int) -> Unit,
) {
    itemsIndexed(payees) { index, payee ->
        ReadOnlyForm(
            labelText = stringResource(R.string.individual_payee_label, index + 1),
            text = payee.name,
            onClickText = { onClickPayeeName(payee.id) },
            supportingText = payee.messageRes,
            icon = painterResource(id = R.drawable.outline_link_off_24),
            iconDescription = stringResource(id = R.string.detach_payee_icon_description),
            onClickIcon = { onClickDetachPayee(payee.id, payee.name) },
        )

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

@Preview(name = "TransferRelationEditContents")
@Composable
private fun Preview() {
    Contents(
        onClickBack = {},
        onClickSave = {},
        paymentName = "リクルートカードプラス",
        paymentNameMessage = null,
        onClickPayment = {},
        onClickDelete = {},
        payer = UiState.Payer.Assigned(
            id = 1,
            name = "三井住友銀行",
        ),
        onClickDetachPayerIcon = {},
        onClickAddPayer = {},
        payees = listOf(
            UiState.Payee(id = 1, name = "電気料金"),
            UiState.Payee(id = 2, name = "水道料金"),
            UiState.Payee(id = 3, name = "ガス料金"),
        ),
        onClickDetachPayeeIcon = { _, _ -> },
        onClickAddPayee = {},
        onClickRelatedName = {},
    )
}

@Preview(name = "TransferRelationEditContents")
@Composable
private fun NoPayerNoPayeePreview() {
    Contents(
        onClickBack = {},
        onClickSave = {},
        paymentName = "リクルートカードプラス",
        paymentNameMessage = null,
        onClickPayment = {},
        onClickDelete = {},
        payer = UiState.Payer.Unassigned,
        onClickDetachPayerIcon = {},
        onClickAddPayer = {},
        payees = listOf(),
        onClickDetachPayeeIcon = { _, _ -> },
        onClickAddPayee = {},
        onClickRelatedName = {},
    )
}
