/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.DestInputType
import com.kurodai0715.directdebitmanager.ui.common_ui.components.EditableForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalThreeButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.ReadOnlyForm
import com.kurodai0715.directdebitmanager.ui.common_ui.components.SingleChoiceSegmentedButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteConfirmDialog
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteNotAllowedDialog
import com.kurodai0715.directdebitmanager.ui.dialog.source_selection.SourceSelectionDialog
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick
import kotlinx.coroutines.launch

@Composable
fun DestinationEditScreen(
    viewModel: DestinationEditViewModel = hiltViewModel(),
    destinationId: Int?,
    onClickNavigateUp: () -> Unit,
    onClickSourceList: () -> Unit,
    onClickSourceEdit: () -> Unit,
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
        val uiLocalState = uiState.uiLocalState
        val formUiState = uiState.formInputState
        val derivedUiState = uiState.derivedUiState
        val persistedDataState = uiState.persistedDataState

        // リスト画面から引き継いだパラメータで UI 状態を初期化する。
        LaunchedEffect(destinationId) {
            viewModel.initialize(destinationId)
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

                    UiEvent.NavigateUp -> onClickNavigateUp()

                    UiEvent.NavigateToSourceList -> onClickSourceList()

                    UiEvent.NavigateToSourceEdit -> onClickSourceEdit()
                }
            }
        }

        DestinationEditContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(LayoutTokens.screenPaddingHalf),
            keyboardDestName = formUiState.destInput.name,
            dialogDestName = formUiState.destInput.name,
            onDestChanged = { viewModel.updateDest(it) },
            sourceName = derivedUiState.sourceName,
            itemId = viewModel.destId,
            selectedButton = formUiState.inputType,
            onSelectDestInputType = { viewModel.updateDestInputType(it) },
            destErrorMessage = uiLocalState.destErrorMessage,
            sourceErrorMessage = uiLocalState.sourceErrorMessage,
            onClickDelete = { viewModel.checkRelatedDataExistence() },
            onClickNavigateUp = { viewModel.requestNavigateUp() },
            onClickSave = { viewModel.validate() },
            onClickSource = { viewModel.onClickSource() },
            onClickDestSelectField = { viewModel.onClickDestSelectField() }
        )

        when (val dialog = uiLocalState.dialog) {
            DestinationEditDialog.DeleteNotAllowed -> {
                DeleteNotAllowedDialog(
                    messageResId = R.string.del_not_allowed_text_in_transfer_edit,
                    onDismissRequest = { viewModel.updateDialogState(null) },
                )
            }

            DestinationEditDialog.DeleteConfirm -> {
                DeleteConfirmDialog(
                    messageResId = R.string.del_conf_text_transfer_info,
                    onDismissRequest = { viewModel.updateDialogState(null) },
                    onClickNo = { /* 処理不要 */ },
                    onClickYes = { viewModel.deleteData() },
                )
            }

            DestinationEditDialog.DeleteCompletion -> {
                DeleteCompletionDialog(
                    onClickClose = {
                        viewModel.updateDialogState(null)
                        viewModel.requestNavigateUp()
                    }
                )
            }

            is DestinationEditDialog.SourceSelection -> {
                SourceSelectionDialog(
                    items = persistedDataState.sourceUiModels,
                    onDismissRequest = { viewModel.updateDialogState(null) },
                    onClickItem = { sourceUiModel ->
                        viewModel.onClickSourceItem(
                            type = dialog.type,
                            itemId = sourceUiModel.sourceId
                        )
                    },
                    onClickAddEdit = {
                        viewModel.updateDialogState(null)
                        viewModel.requestNavigateToSourceList()
                    }
                )
            }

            DestinationEditDialog.NoSourceData -> {
                NoSourceDataDialog(
                    onDismissRequest = {
                        viewModel.updateDialogState(null)
                    },
                    onClickRegister = {
                        viewModel.updateDialogState(null)
                        viewModel.requestNavigateToSourceEdit()
                    },
                )
            }

            null -> Unit
        }

        if (uiLocalState.isLoading) {
            AppUncertainCircularIndicator()
        }
    }
}

@Composable
fun DestinationEditContents(
    modifier: Modifier = Modifier,
    keyboardDestName: String,
    dialogDestName: String,
    onDestChanged: (String) -> Unit,
    sourceName: String,
    itemId: Int?,
    selectedButton: DestInputType,
    onSelectDestInputType: (DestInputType) -> Unit,
    destErrorMessage: Int?,
    sourceErrorMessage: Int?,
    onClickDelete: () -> Unit,
    onClickNavigateUp: () -> Unit,
    onClickSave: () -> Unit,
    onClickSource: () -> Unit,
    onClickDestSelectField: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents(
                sourceName = sourceName,
                onClickSource = onClickSource,
                sourceErrorMessage = sourceErrorMessage,
                selectedButton = selectedButton,
                onSelectDestInputType = onSelectDestInputType,
                keyboardDestName = keyboardDestName,
                onDestChanged = onDestChanged,
                destErrorMessage = destErrorMessage,
                dialogDestName = dialogDestName,
                onClickDestSelectField = onClickDestSelectField
            )
        },
        bottomButton = { BottomButton(itemId, onClickDelete, onClickNavigateUp, onClickSave) }
    )
}

@Composable
private fun Contents(
    sourceName: String,
    onClickSource: () -> Unit,
    sourceErrorMessage: Int?,
    selectedButton: DestInputType,
    onSelectDestInputType: (DestInputType) -> Unit,
    keyboardDestName: String,
    onDestChanged: (String) -> Unit,
    destErrorMessage: Int?,
    dialogDestName: String,
    onClickDestSelectField: () -> Unit
) {
    Column {
        ReadOnlyForm(
            labelText = stringResource(R.string.source_text_label),
            text = sourceName,
            onClickText = onClickSource,
            supportingText = sourceErrorMessage,
            icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
            iconDescription = stringResource(id = R.string.open_source_list_dialog_icon_description),
            onClickIcon = onClickSource,
        )

        Spacer(modifier = Modifier.height(LayoutTokens.itemSpacing))

        SingleChoiceSegmentedButton(
            modifier = Modifier.fillMaxWidth(),
            selectedIndex = selectedButton.displayIndex,
            label = stringResource(R.string.destination_input_type_label),
            buttonLabels = DestInputType.getSortedList().map { it.label() },
            onSelected = { index -> onSelectDestInputType(DestInputType.fromInt(index)) },
        )

        Spacer(modifier = Modifier.height(LayoutTokens.itemSpacing))

        if (selectedButton == DestInputType.Keyboard) {
            EditableForm(
                labelText = stringResource(R.string.destination_text_label),
                text = keyboardDestName,
                onTextChanged = onDestChanged,
                supportingText = destErrorMessage,
                onClickClear = { onDestChanged("") }
            )
        } else if (selectedButton == DestInputType.SourceList) {
            ReadOnlyForm(
                labelText = stringResource(R.string.destination_text_label),
                text = dialogDestName,
                onClickText = onClickDestSelectField,
                supportingText = destErrorMessage,
                icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
                iconDescription = stringResource(id = R.string.open_destination_list_dialog_icon_description),
                onClickIcon = onClickDestSelectField,
            )
        }
    }
}

@Composable
private fun BottomButton(
    itemId: Int?,
    onClickDelete: () -> Unit,
    onClickNavigateUp: () -> Unit,
    onClickSave: () -> Unit
) {
    // TODO 振替先を振替元から選択すると新規登録でも削除ボタンが出てくる動作がいまいちなので、修正する。
    if (itemId != 0 && itemId != null) {
        HorizontalThreeButton(
            onClickLeft = { debouncedClick(onClickDelete) },
            onClickCenter = { debouncedClick(onClickNavigateUp) },
            onClickRight = { debouncedClick(onClickSave) },
            leftText = stringResource(R.string.common_delete),
            centerText = stringResource(R.string.common_back),
            rightText = stringResource(R.string.common_update)
        )
    } else {
        HorizontalTwoButton(
            onClickLeft = { debouncedClick(onClickNavigateUp) },
            onClickRight = { debouncedClick(onClickSave) },
            leftText = stringResource(R.string.common_back),
            rightText = stringResource(R.string.common_save)
        )
    }
}

@Suppress("ComposableNaming")
@Composable
private fun DestInputType.label(): String {
    return when (this) {
        DestInputType.Keyboard -> stringResource(R.string.keyboard_input)
        DestInputType.SourceList -> stringResource(R.string.select_from_source)
    }
}

@Preview(name = "DestinationEditContents")
@Composable
private fun PreviewUpdateContents() {
    DestinationEditContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(LayoutTokens.screenPaddingHalf),
        keyboardDestName = "横浜銀行クレジットカード",
        dialogDestName = "",
        onDestChanged = {},
        sourceName = "横浜銀行",
        itemId = 1,
        selectedButton = DestInputType.Keyboard,
        onSelectDestInputType = {},
        destErrorMessage = null,
        sourceErrorMessage = null,
        onClickDelete = {},
        onClickNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickDestSelectField = {},
    )
}

@Preview(name = "DestinationEditContents")
@Composable
private fun PreviewRegisterContents() {
    DestinationEditContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(LayoutTokens.screenPaddingHalf),
        keyboardDestName = "横浜銀行クレジットカード",
        dialogDestName = "",
        onDestChanged = {},
        sourceName = "横浜銀行",
        itemId = 0,
        selectedButton = DestInputType.SourceList,
        onSelectDestInputType = {},
        destErrorMessage = null,
        sourceErrorMessage = null,
        onClickDelete = {},
        onClickNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickDestSelectField = {},
    )
}

@Preview(name = "DestinationEditContents")
@Composable
private fun PreviewEmptyTextContents() {
    DestinationEditContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(LayoutTokens.screenPaddingHalf),
        keyboardDestName = "",
        dialogDestName = "",
        onDestChanged = {},
        sourceName = "",
        itemId = 0,
        selectedButton = DestInputType.Keyboard,
        onSelectDestInputType = {},
        destErrorMessage = null,
        sourceErrorMessage = null,
        onClickDelete = {},
        onClickNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickDestSelectField = {},
    )
}

@Preview(name = "DestinationEditContents")
@Composable
private fun PreviewValidationErrorContents() {
    DestinationEditContents(
        modifier = Modifier
            .fillMaxSize()
            .padding(LayoutTokens.screenPaddingHalf),
        keyboardDestName = "",
        dialogDestName = "",
        onDestChanged = {},
        sourceName = "",
        itemId = 0,
        selectedButton = DestInputType.SourceList,
        onSelectDestInputType = {},
        destErrorMessage = R.string.common_required_field,
        sourceErrorMessage = R.string.common_required_field,
        onClickDelete = {},
        onClickNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickDestSelectField = {},
    )
}
