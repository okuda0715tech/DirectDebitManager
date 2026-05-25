package com.kurodai0715.directdebitmanager.ui.screen.payee_edit

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalThreeButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.BodyBottomButtonLayout
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

@Composable
fun PayeeEditScreen(
    viewModel: PayeeEditViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
    payeeId: Int?,
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
        LaunchedEffect(payeeId) {
            viewModel.initialize(payeeId)
        }

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is PayeeEditUiEvent.ShowSnackbar -> launch {
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

        PayeeEditContents(
            modifier = Modifier.padding(paddingValues),
            mode = uiState.editMode,
            payeeName = uiState.payeeName,
            onPayeeNameChanged = { viewModel.updatePayeeName(it) },
            onClickSave = { viewModel.validate() },
            onClickBack = onClickBack,
            supportingTextRes = uiState.payeeErrorMessage
        )
    }
}

@Composable
fun PayeeEditContents(
    modifier: Modifier = Modifier,
    mode: PayeeEditMode,
    payeeName: String,
    onPayeeNameChanged: (String) -> Unit,
    onClickSave: () -> Unit,
    onClickBack: () -> Unit,
    supportingTextRes: Int?
) {
    BodyBottomButtonLayout(
        modifier = modifier,
        body = {
            Contents(
                payeeName = payeeName,
                onPayeeNameChanged = onPayeeNameChanged,
                supportingTextRes = supportingTextRes
            )
        },
        bottomButton = {
            BottomButton(mode, onClickBack, onClickSave)
        }
    )
}

@Composable
private fun BottomButton(
    mode: PayeeEditMode,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit
) {
    when (mode) {
        is PayeeEditMode.Add -> {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick(onClickSave) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_save)
            )
        }

        is PayeeEditMode.Edit -> {
            HorizontalThreeButton(
                onClickLeft = { debouncedClick(TODO("削除するには振替関係を考慮する必要があるが、振替関係のモデルが未実装のため、後で実装する")) },
                onClickCenter = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick(onClickSave) },
                leftText = stringResource(R.string.common_delete),
                centerText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_update)
            )
        }
    }
}

@Composable
fun Contents(
    payeeName: String,
    onPayeeNameChanged: (String) -> Unit,
    supportingTextRes: Int?
) {
    Column {
        EditableForm(
            labelText = stringResource(R.string.payee_name_label_old),
            text = payeeName,
            onTextChanged = onPayeeNameChanged,
            supportingText = supportingTextRes,
            onClickClear = { onPayeeNameChanged("") }
        )
    }
}

@Preview(name = "PayeeEditContents")
@Composable
private fun PreviewAddModeContents() {
    PayeeEditContents(
        mode = PayeeEditMode.Add,
        payeeName = "",
        onPayeeNameChanged = {},
        onClickBack = {},
        onClickSave = {},
        supportingTextRes = null,
    )
}

@Preview(name = "PayeeEditContents")
@Composable
private fun PreviewEditModeContents() {
    PayeeEditContents(
        mode = PayeeEditMode.Edit(1),
        payeeName = "電気料金",
        onPayeeNameChanged = {},
        onClickBack = {},
        onClickSave = {},
        supportingTextRes = null,
    )
}
