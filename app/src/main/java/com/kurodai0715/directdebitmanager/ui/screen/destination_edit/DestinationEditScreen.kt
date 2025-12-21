/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.DestInputType
import com.kurodai0715.directdebitmanager.ui.common_ui.components.DisplayTextFormField
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalThreeButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.components.KeyboardEditableFormField
import com.kurodai0715.directdebitmanager.ui.common_ui.components.SingleChoiceSegmentedButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.AppUncertainCircularIndicator
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteConfirmDialog
import com.kurodai0715.directdebitmanager.ui.dialog.DeleteNotAllowedDialog
import com.kurodai0715.directdebitmanager.ui.dialog.source_selection.SourceListDialogType
import com.kurodai0715.directdebitmanager.ui.dialog.source_selection.SourceSelectionDialog
import com.kurodai0715.directdebitmanager.ui.theme.LIST_ITEM_SPACE_DEF
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
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
        val formUiState = uiState.formUiState

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
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            keyboardInputDestName = formUiState.destInput.name,
            dialogSelectionDestName = formUiState.destInput.name,
            onDestChanged = { viewModel.updateDest(it) },
            sourceName = formUiState.sourceName,
            itemId = viewModel.destId,
            selectedButton = formUiState.selectedButton,
            onSelectDestInputType = { viewModel.updateDestInputType(it) },
            destErrorMessage = uiLocalState.destErrorMessage,
            sourceErrorMessage = uiLocalState.sourceErrorMessage,
            onClickDelete = { viewModel.checkRelatedDataExistence() },
            onClickNavigateUp = { viewModel.requestNavigateUp() },
            onClickSave = { viewModel.validate() },
            onClickSource = { viewModel.updateSourceListDialogType(SourceListDialogType.Source) },
            onClickDestSelectField = { viewModel.updateSourceListDialogType(SourceListDialogType.Destination) }
        )

        when (uiLocalState.destinationEditDialog) {
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

            null -> Unit
        }

        if (uiLocalState.sourceListDialogType != null) {

            val type = uiLocalState.sourceListDialogType

            if (uiState.sourceSelectionDialogItems.isNotEmpty()) {
                SourceSelectionDialog(
                    items = uiState.sourceSelectionDialogItems,
                    onDismissRequest = { viewModel.updateSourceListDialogType(null) },
                    onClickItem = { sourceUiModel ->
                        when (type) {
                            SourceListDialogType.Source -> viewModel.updateSource(
                                sourceId = sourceUiModel.sourceId
                            )

                            SourceListDialogType.Destination -> viewModel.updateDestFromDialog(
                                destId = sourceUiModel.sourceId
                            )
                        }
                    },
                    onClickAddEdit = {
                        viewModel.updateSourceListDialogType(null)
                        viewModel.requestNavigateToSourceList()
                    }
                )
            } else {
                NoSourceDataDialog(
                    onDismissRequest = {
                        viewModel.updateSourceListDialogType(null)
                    },
                    onClickRegister = {
                        viewModel.updateSourceListDialogType(null)
                        viewModel.requestNavigateToSourceEdit()
                    },
                )
            }
        }

        if (uiLocalState.isLoading) {
            AppUncertainCircularIndicator()
        }
    }
}

@Composable
fun DestinationEditContents(
    modifier: Modifier = Modifier,
    keyboardInputDestName: String,
    dialogSelectionDestName: String,
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        DisplayTextFormField(
            labelText = stringResource(R.string.source_text_label),
            text = sourceName,
            onClickText = onClickSource,
            supportingText = sourceErrorMessage,
            icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
            iconDescription = stringResource(id = R.string.open_source_list_dialog_icon_description),
            onClickIcon = onClickSource,
        )

        Spacer(modifier = Modifier.height(LIST_ITEM_SPACE_DEF))

        SingleChoiceSegmentedButton(
            modifier = Modifier.fillMaxWidth(),
            selectedIndex = selectedButton.displayIndex,
            label = stringResource(R.string.destination_input_type_label),
            buttonLabels = DestInputType.getSortedList().map { it.label() },
            onSelected = { index -> onSelectDestInputType(DestInputType.fromInt(index)) },
        )

        Spacer(modifier = Modifier.height(LIST_ITEM_SPACE_DEF))

        if (selectedButton == DestInputType.Keyboard) {
            KeyboardEditableFormField(
                labelText = stringResource(R.string.destination_text_label),
                text = keyboardInputDestName,
                onTextChanged = onDestChanged,
                supportingText = destErrorMessage,
                onClickClear = { onDestChanged("") }
            )
        } else if (selectedButton == DestInputType.SourceList) {
            DisplayTextFormField(
                labelText = stringResource(R.string.destination_text_label),
                text = dialogSelectionDestName,
                onClickText = onClickDestSelectField,
                supportingText = destErrorMessage,
                icon = painterResource(id = R.drawable.outline_arrow_drop_down_circle_24),
                iconDescription = stringResource(id = R.string.open_destination_list_dialog_icon_description),
                onClickIcon = onClickDestSelectField,
            )
        }

        Spacer(modifier = Modifier.height(LIST_ITEM_SPACE_DEF))

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
        keyboardInputDestName = "横浜銀行クレジットカード",
        dialogSelectionDestName = "",
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
        keyboardInputDestName = "横浜銀行クレジットカード",
        dialogSelectionDestName = "",
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
        keyboardInputDestName = "",
        dialogSelectionDestName = "",
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
        keyboardInputDestName = "",
        dialogSelectionDestName = "",
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
