package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.BodyBottomButtonLayout
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

@Composable
fun Screen(
    paymentId: Int?,
    viewModel: ViewModel = hiltViewModel(),
    onClickBack: () -> Unit,
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
                }
            }
        }

        PaymentEditContents(
            modifier = Modifier.padding(paddingValues),
            name = uiState.name,
            nameValidation = uiState.nameValidation,
            onChangeName = { viewModel.updateName(it) },
            onClickClear = { viewModel.updateName("") },
            onClickBack = onClickBack,
            onClickSave = { viewModel.onClickSave() },
        )
    }
}

@Composable
fun PaymentEditContents(
    modifier: Modifier = Modifier,
    name: String,
    nameValidation: NameValidation,
    onChangeName: (String) -> Unit,
    onClickClear: () -> Unit,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
) {
    BodyBottomButtonLayout(
        modifier = modifier,
        body = {
            Contents(
                name = name,
                nameValidation = nameValidation,
                onChangeName = onChangeName,
                onClickClear = onClickClear,
            )
        },
        bottomButton = {
            BottomButton(onClickBack, onClickSave)
        }
    )
}

@Composable
fun Contents(
    name: String,
    nameValidation: NameValidation,
    onChangeName: (String) -> Unit,
    onClickClear: () -> Unit,
) {
    Column {
        Text(stringResource(R.string.payment_edit_screen_description))

        Spacer(modifier = Modifier.size(LayoutTokens.itemSpacing))

        EditableForm(
            labelText = stringResource(R.string.payment_name_label2),
            text = name,
            onTextChanged = onChangeName,
            supportingText = when (nameValidation) {
                NameValidation.Valid -> null
                NameValidation.EmptyError -> R.string.common_required_field
                NameValidation.LengthOver100Error -> R.string.common_length_needs_to_be_within_100
            },
            onClickClear = onClickClear
        )
    }

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