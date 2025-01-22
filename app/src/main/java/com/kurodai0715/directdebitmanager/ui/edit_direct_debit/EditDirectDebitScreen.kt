package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import com.kurodai0715.directdebitmanager.ui.theme.ICON_EX_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun EditDirectDebitScreen(
    viewModel: EditDirectDebitViewModel = hiltViewModel(),
    directDebit: DirectDebit?,
    onNavigateUp: () -> Unit,
    onNavigateToDelComp: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState,
            // Snackbar がキーボードで隠れないようにする。
            modifier = Modifier.safeDrawingPadding()
        )
    }) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        // リスト画面から引き継いだパラメータで UI 状態を初期化する。
        LaunchedEffect(directDebit) {
            if (directDebit != null) {
                viewModel.updateDirectDebit(directDebit)
            }
        }

        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.clearMessage()
            }
        }

        EditDirectDebitContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            transferDest = uiState.transferDest,
            onDestChanged = { viewModel.updateDest(it) },
            transferSource = uiState.transferSource,
            onSourceChanged = { viewModel.updateSource(it) },
            itemId = uiState.id,
            onClickDelete = { viewModel.updateDelConfDialogVisibility(true) },
            onNavigateUp = onNavigateUp,
            onClickSave = { viewModel.saveData() },
        )

        if (uiState.showDelConfDialog) {
            DeleteConfirmDialog(
                onDismissRequest = { viewModel.updateDelConfDialogVisibility(false) },
                onClickNo = { /* 処理不要 */ },
                onClickYes = { viewModel.deleteData() },
            )
        }

        LaunchedEffect(uiState.showDelCompDialog) {
            if (uiState.showDelCompDialog) {
                // 削除確認ダイアログは、単なる AlertDialog() コンポーザブルで実装しているが、
                // 削除完了ダイアログは、 Navigation コンポーネントのダイアログデスティネーションとして実装している。
                //
                // その理由は、削除完了ダイアログを閉じた際に、変更画面を閉じて、一覧画面にポップバックする必要があります。
                // もし、削除完了ダイアログで、ダイアログデスティネーションを使用しなかった場合、
                // 削除完了ダイアログの「閉じる」ボタンをタップした際に、
                // 変更画面が一覧画面に切り替わった後に、削除完了ダイアログが閉じるという、処理順序の逆転が発生してしまう。
                // それを避けるために、削除完了ダイアログでは、ダイアログデスティネーションを使用しています。
                onNavigateToDelComp()
            }
        }
    }
}

@Composable
fun EditDirectDebitContents(
    modifier: Modifier = Modifier,
    transferDest: String,
    onDestChanged: (String) -> Unit,
    transferSource: String,
    onSourceChanged: (String) -> Unit,
    itemId: Int,
    onClickDelete: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickSave: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = transferDest,
            onValueChange = onDestChanged,
            label = { Text(stringResource(R.string.transfer_dest)) },
            modifier = Modifier.fillMaxWidth(),
        )
        TextField(
            value = transferSource,
            onValueChange = onSourceChanged,
            label = { Text(stringResource(R.string.transfer_source)) },
            modifier = Modifier.fillMaxWidth(),
        )
//        DatePickerText(onTextChanged = {
//            viewModel.updateDate(it)
//        })
//        TextField(
//            value = uiState.transferAmount.toString(),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            onValueChange = { viewModel.updateAmount(it) },
//            label = { Text(stringResource(R.string.transfer_amount)) },
//            modifier = Modifier.fillMaxWidth(),
//        )
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (itemId != 0) {
                TextButton(onClick = { debouncedClick(onClickDelete) }) {
                    Text(
                        text = stringResource(R.string.common_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            OutlinedButton(onClick = {
                Log.v(TAG, "back is clicked.")
                debouncedClick(onNavigateUp)
            }) {
                Text(stringResource(R.string.common_back))
            }
            Button(onClick = { debouncedClick(onClickSave) }) {
                Text(stringResource(R.string.common_save))
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    onDismissRequest: () -> Unit,
    onClickNo: () -> Unit,
    onClickYes: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_outline_24),
                contentDescription = stringResource(id = R.string.del_conf_icon_description),
                modifier = Modifier.size(ICON_EX_LARGE_SIZE),
            )
        },
        title = {
            Text(text = stringResource(R.string.del_conf_title))
        },
        text = {
            Text(text = stringResource(R.string.del_conf_text))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                debouncedClick {
                    onClickYes()
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                debouncedClick {
                    onClickNo()
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_no))
            }
        })
}

@Preview
@Composable
private fun PreviewRegisterContents() {
    EditDirectDebitContents(
        transferDest = "横浜銀行クレジットカード",
        onDestChanged = {},
        transferSource = "横浜銀行",
        onSourceChanged = {},
        itemId = 0,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
    )
}

@Preview
@Composable
private fun PreviewUpdateContents() {
    EditDirectDebitContents(
        transferDest = "横浜銀行クレジットカード",
        onDestChanged = {},
        transferSource = "横浜銀行",
        onSourceChanged = {},
        itemId = 1,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
    )
}

@Preview
@Composable
private fun PreviewDelConfDialog() {
    DeleteConfirmDialog(
        onDismissRequest = {},
        onClickNo = {},
        onClickYes = {}
    )
}