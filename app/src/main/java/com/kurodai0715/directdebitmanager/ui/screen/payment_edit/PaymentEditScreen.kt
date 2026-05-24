package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
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
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentEditScreen(
    viewModel: PaymentEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentEditContents(
        name = uiState.name,
        onClickBack = { TODO() },
        onClickSave = { TODO() },
    )
}

@Composable
fun PaymentEditContents(
    modifier: Modifier = Modifier,
    name: String,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(name)
        },
        bottomButton = {
            BottomButton(onClickBack, onClickSave)
        }
    )
}

@Composable
fun Contents(name: String) {
    Column {
        Text(stringResource(R.string.payment_edit_screen_description))

        Spacer(modifier = Modifier.size(LayoutTokens.itemSpacing))

        EditableForm(
            labelText = stringResource(R.string.payment_name_label2),
            text = name,
            onTextChanged = { TODO() },
            supportingText = null, // TODO
            onClickClear = { TODO() }
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