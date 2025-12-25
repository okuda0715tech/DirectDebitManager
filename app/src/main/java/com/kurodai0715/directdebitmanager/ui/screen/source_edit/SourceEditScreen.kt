/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.DisplayTextFormField
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalThreeButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableFormField
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteConfirmDialog
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteNotAllowedDialog
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceEditScreen(
    viewModel: SourceEditViewModel = hiltViewModel(),
    sourceId: Int?,
    onClickNavigateUp: () -> Unit,
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
        LaunchedEffect(sourceId) {
            if (sourceId != null) {
                viewModel.initialize(sourceId)
            }
        }

        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.clearMessage()
            }
        }

        SourceEditContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(LayoutTokens.screenPaddingHalf),
            source = uiState.sourceName,
            onSourceChanged = { viewModel.updateSource(it) },
            itemId = uiState.sourceId,
            sourceTypeStringRes = getSourceTypeStringRes(uiState.sourceType),
            sourceErrorMessage = uiState.sourceErrorMessage,
            onClickDelete = { viewModel.checkRelatedDataExistence(uiState.sourceId) },
            onNavigateUp = onClickNavigateUp,
            onClickSave = { viewModel.validate() },
            onClickType = { viewModel.updateSourceTypeListDialogVisibility(true) }
        )

        if (uiState.showDelNotAllowedDialog) {
            DeleteNotAllowedDialog(
                messageResId = R.string.del_not_allowed_text_in_source_edit,
                onDismissRequest = { viewModel.updateDelNotAllowedDialogVisibility(false) },
            )
        }

        if (uiState.showDelConfDialog) {
            DeleteConfirmDialog(
                messageResId = R.string.del_conf_text_source_info,
                onDismissRequest = { viewModel.updateDelConfDialogVisibility(false) },
                onClickNo = { /* 処理不要 */ },
                onClickYes = { viewModel.deleteData() },
            )
        }

        if (uiState.showDelCompDialog) {
            DeleteCompletionDialog(
                onClickClose = {
                    viewModel.updateDelCompDialogVisibility(false)
                    viewModel.updateShouldNavigateUp(true)
                }
            )
        }

        LaunchedEffect(uiState.shouldNavigateUp) {
            if (uiState.shouldNavigateUp) {
                viewModel.updateShouldNavigateUp(false)
                onClickNavigateUp()
            }
        }

        if (uiState.showSourceTypeListDialog) {
            SourceTypeListDialog(
                onDismissRequest = { viewModel.updateSourceTypeListDialogVisibility(false) },
                onClickItem = { viewModel.updateSourceType(it) },
            )
        }
    }
}

@Composable
fun SourceEditContents(
    modifier: Modifier = Modifier,
    source: String,
    onSourceChanged: (String) -> Unit,
    itemId: Int,
    sourceTypeStringRes: Int,
    sourceErrorMessage: Int?,
    onClickDelete: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickSave: () -> Unit,
    onClickType: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(source, onSourceChanged, sourceErrorMessage, sourceTypeStringRes, onClickType)
        },
        bottomButton = {
            BottomButton(itemId, onClickDelete, onNavigateUp, onClickSave)
        }
    )
}

@Composable
private fun BottomButton(
    itemId: Int,
    onClickDelete: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickSave: () -> Unit
) {
    if (itemId != 0) {
        HorizontalThreeButton(
            onClickLeft = { debouncedClick(onClickDelete) },
            onClickCenter = { debouncedClick(onNavigateUp) },
            onClickRight = { debouncedClick(onClickSave) },
            leftText = stringResource(R.string.common_delete),
            centerText = stringResource(R.string.common_back),
            rightText = stringResource(R.string.common_update)
        )
    } else {
        HorizontalTwoButton(
            onClickLeft = { debouncedClick(onNavigateUp) },
            onClickRight = { debouncedClick(onClickSave) },
            leftText = stringResource(R.string.common_back),
            rightText = stringResource(R.string.common_save)
        )
    }
}

@Composable
private fun Contents(
    source: String,
    onSourceChanged: (String) -> Unit,
    sourceErrorMessage: Int?,
    sourceTypeStringRes: Int,
    onClickType: () -> Unit
) {
    Column {
        EditableFormField(
            labelText = stringResource(R.string.source_edit_text_label),
            text = source,
            onTextChanged = onSourceChanged,
            supportingText = sourceErrorMessage,
            onClickClear = { onSourceChanged("") }
        )

        Spacer(modifier = Modifier.height(LayoutTokens.itemSpacing))

        DisplayTextFormField(
            labelText = stringResource(R.string.source_type),
            text = stringResource(sourceTypeStringRes),
            onClickText = onClickType,
            supportingText = null,
            icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
            iconDescription = stringResource(id = R.string.open_source_type_dialog_icon_description),
            onClickIcon = onClickType,
        )
    }
}

@Preview
@Composable
private fun PreviewUpdateContents() {
    SourceEditContents(
        modifier = Modifier.padding(LayoutTokens.screenPaddingHalf),
        source = "横浜銀行",
        onSourceChanged = {},
        itemId = 1,
        sourceTypeStringRes = R.string.bank,
        sourceErrorMessage = null,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickType = {},
    )
}

@Preview
@Composable
private fun PreviewRegisterContents() {
    SourceEditContents(
        modifier = Modifier.padding(LayoutTokens.screenPaddingHalf),
        source = "横浜銀行",
        onSourceChanged = {},
        itemId = 0,
        sourceTypeStringRes = R.string.bank,
        sourceErrorMessage = null,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickType = {},
    )
}

@Preview
@Composable
private fun PreviewValidationErrorContents() {
    SourceEditContents(
        modifier = Modifier.padding(LayoutTokens.screenPaddingHalf),
        source = "横浜銀行",
        onSourceChanged = {},
        itemId = 0,
        sourceTypeStringRes = R.string.bank,
        sourceErrorMessage = R.string.common_required_field,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickType = {},
    )
}
