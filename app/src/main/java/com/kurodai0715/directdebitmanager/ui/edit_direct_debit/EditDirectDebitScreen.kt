package com.kurodai0715.directdebitmanager.ui.edit_direct_debit

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import com.kurodai0715.directdebitmanager.data.source.TransSource
import com.kurodai0715.directdebitmanager.ui.component.SurfaceButton
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
            itemId = uiState.id,
            onClickDelete = { viewModel.updateDelConfDialogVisibility(true) },
            onNavigateUp = onNavigateUp,
            onClickSave = { viewModel.saveData() },
            onClickSource = { viewModel.updateSourceListDialogVisibility(true) },
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

        if (uiState.showSourceListDialog) {
            if (uiState.sources.isNotEmpty()) {
                SourceListDialog(
                    items = uiState.sources,
                    onDismissRequest = { viewModel.updateSourceListDialogVisibility(false) },
                    onClickItem = { index ->
                        val transSource = uiState.sources[index]
                        viewModel.updateSource(
                            sourceId = transSource.id,
                            source = transSource.source
                        )
                    }
                )
            } else {
                NoSourceDataDialog(onDismissRequest = {
                    viewModel.updateSourceListDialogVisibility(
                        false
                    )
                })
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
    itemId: Int,
    onClickDelete: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickSave: () -> Unit,
    onClickSource: () -> Unit,
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
        SurfaceButton(
            onClick = {
                debouncedClick {
                    onClickSource()
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                if (transferSource.isEmpty()) {
                    Text(
                        text = stringResource(R.string.transfer_source),
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.transfer_source),
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transferSource,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
fun SourceListDialog(
    modifier: Modifier = Modifier,
    items: List<TransSource>,
    onDismissRequest: () -> Unit,
    onClickItem: (Int) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            LazyColumn(modifier = Modifier.padding(SCREEN_EDGE_PADDING_DEF)) {
                itemsIndexed(items) { index, item ->

                    SurfaceButton(
                        onClick = {
                            debouncedClick {
                                onClickItem(index)
                                onDismissRequest()
                            }
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Text(
                            text = item.source,
                            modifier = modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                .padding(12.dp)
                        )
                    }

                    // 最後のアイテム以外なら分割線を引く
                    if (index != items.size - 1) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun NoSourceDataDialog(
    onDismissRequest: () -> Unit,
) {

    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_outline_24),
                contentDescription = stringResource(id = R.string.usage_rules_icon_description),
                modifier = Modifier.size(ICON_EX_LARGE_SIZE),
            )
        },
        title = {
            Text(text = stringResource(R.string.no_source_data_title))
        },
        text = {
            Text(text = stringResource(R.string.no_source_data_text))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                debouncedClick {
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_close))
            }
        },
    )
}

@Preview
@Composable
private fun PreviewRegisterContents() {
    EditDirectDebitContents(
        transferDest = "横浜銀行クレジットカード",
        onDestChanged = {},
        transferSource = "横浜銀行",
        itemId = 0,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
    )
}

@Preview
@Composable
private fun PreviewUpdateContents() {
    EditDirectDebitContents(
        transferDest = "横浜銀行クレジットカード",
        onDestChanged = {},
        transferSource = "横浜銀行",
        itemId = 1,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
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

@Preview
@Composable
private fun PreviewSourceListDialog() {
    SourceListDialog(
        items = listOf(
            TransSource(id = 1, source = "横浜銀行"),
            TransSource(id = 2, source = "三井住友銀行"),
            TransSource(id = 3, source = "PayPay銀行"),
        ),
        onDismissRequest = {},
        onClickItem = {},
    )
}

@Preview
@Composable
private fun PreviewNoSourceDataDialog() {
    NoSourceDataDialog(
        onDismissRequest = {},
    )
}