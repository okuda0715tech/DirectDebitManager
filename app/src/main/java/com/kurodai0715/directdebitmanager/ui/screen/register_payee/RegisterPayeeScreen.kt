package com.kurodai0715.directdebitmanager.ui.screen.register_payee

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun RegisterPayeeScreen(
    viewModel: RegisterPayeeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterPayeeContents(
        modifier = Modifier,
        payeeName = uiState.payeeName,
        onPayeeNameChanged = { viewModel.updatePayeeName(it) },
        supportingTextRes = null
    )
}

@Composable
fun RegisterPayeeContents(
    modifier: Modifier = Modifier,
    payeeName: String,
    onPayeeNameChanged: (String) -> Unit,
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
                onClickLeft = { debouncedClick(TODO()) },
                onClickRight = { debouncedClick(TODO()) },
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