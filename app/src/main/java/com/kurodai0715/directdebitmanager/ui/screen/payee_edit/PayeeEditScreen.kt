package com.kurodai0715.directdebitmanager.ui.screen.payee_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

@Composable
fun PayeeEditScreen(
    viewModel: PayeeEditViewModel = hiltViewModel(),
    onClickNavigateUp: () -> Unit,
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
                    is RegisterPayeeUiEvent.ShowSnackbar -> launch {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(LayoutTokens.screenPaddingHalf),
            payeeName = uiState.payeeName,
            onPayeeNameChanged = { viewModel.updatePayeeName(it) },
            onClickSave = { viewModel.validate() },
            onNavigateUp = onClickNavigateUp,
            supportingTextRes = uiState.payeeErrorMessage
        )
    }
}

@Composable
fun PayeeEditContents(
    modifier: Modifier = Modifier,
    payeeName: String,
    onPayeeNameChanged: (String) -> Unit,
    onClickSave: () -> Unit,
    onNavigateUp: () -> Unit,
    supportingTextRes: Int?
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(
                payeeName = payeeName,
                onPayeeNameChanged = onPayeeNameChanged,
                supportingTextRes = supportingTextRes
            )
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onNavigateUp) },
                onClickRight = { debouncedClick(onClickSave) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_save)
            )
        }
    )
}

@Composable
fun Contents(
    payeeName: String,
    onPayeeNameChanged: (String) -> Unit,
    supportingTextRes: Int?
) {
    Column {
        EditableForm(
            labelText = stringResource(R.string.source_edit_text_label),
            text = payeeName,
            onTextChanged = onPayeeNameChanged,
            supportingText = supportingTextRes,
            onClickClear = { onPayeeNameChanged("") }
        )
    }
}