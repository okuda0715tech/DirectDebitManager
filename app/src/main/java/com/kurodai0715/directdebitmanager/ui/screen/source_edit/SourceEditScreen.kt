package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.ui.common_ui.AppTextField
import com.kurodai0715.directdebitmanager.ui.common_ui.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.common_ui.HorizontalThreeButton
import com.kurodai0715.directdebitmanager.ui.common_ui.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.SelectableText
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceEditScreen(
    viewModel: SourceEditViewModel = hiltViewModel(),
    source: Source?,
    onNavigateUp: () -> Unit,
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
        LaunchedEffect(source) {
            if (source != null) {
                viewModel.updateTransSource(source)
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
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            source = uiState.sourceName,
            onSourceChanged = { viewModel.updateSource(it) },
            itemId = uiState.sourceId,
            sourceTypeStringRes = getSourceTypeStringRes(uiState.sourceType),
            sourceErrorMessage = uiState.sourceErrorMessage,
            onClickDelete = { viewModel.checkRelatedDataExists(uiState.sourceId) },
            onNavigateUp = onNavigateUp,
            onClickSave = { viewModel.validate() },
            onClickType = { viewModel.updateSourceTypeListDialogVisibility(true) }
        )

        if (uiState.showDelConfDialog) {
            SourceEditDeleteConfirmDialog(
                relatedDestCount = uiState.sourceRelatedDestCount,
                onDismissRequest = { viewModel.updateDelConfDialogVisibility(false) },
                onClickNo = { /* 処理不要 */ },
                onClickYes = { viewModel.deleteData() },
            )
        }

        if (uiState.showDelCompDialog) {
            DeleteCompletionDialog(
                onClickClose = {
                    viewModel.updateDelCompDialogVisibility(false)
                    viewModel.updateNavigateUpEventConsumed(false)
                }
            )
        } else {
            if (!uiState.navigationUpEventConsumed) {
                onNavigateUp()
                viewModel.updateNavigateUpEventConsumed(true)
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AppTextField(
            labelText = stringResource(R.string.source_text_label),
            text = source,
            onTextChanged = onSourceChanged,
            supportingText = sourceErrorMessage,
            onClickClear = { onSourceChanged("") }
        )

        SelectableText(
            labelText = stringResource(R.string.source_type),
            text = stringResource(sourceTypeStringRes),
            onClickText = onClickType,
            supportingText = null,
            icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
            iconDescription = stringResource(id = R.string.open_source_type_dialog_icon_description),
            onClickIcon = onClickType,
        )

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
}

@Preview
@Composable
private fun PreviewUpdateContents() {
    SourceEditContents(
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
