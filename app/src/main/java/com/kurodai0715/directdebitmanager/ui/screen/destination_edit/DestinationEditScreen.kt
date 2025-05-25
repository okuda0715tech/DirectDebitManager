package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.ui.common_ui.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.common_ui.DeleteConfirmDialog
import com.kurodai0715.directdebitmanager.ui.common_ui.HorizontalThreeButton
import com.kurodai0715.directdebitmanager.ui.common_ui.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.SurfaceButton
import com.kurodai0715.directdebitmanager.ui.theme.ICON_EX_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun DestinationEditScreen(
    viewModel: DestinationEditViewModel = hiltViewModel(),
    destination: Destination?,
    onNavigateUp: () -> Unit,
    onNavigateToSourceList: () -> Unit,
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
        LaunchedEffect(destination) {
            if (destination != null) {
                viewModel.updateDirectDebit(destination)
            }
        }

        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(snackbarText) {
                snackbarHostState.showSnackbar(snackbarText)
                viewModel.clearMessage()
            }
        }

        DestinationEditContents(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(SCREEN_EDGE_PADDING_DEF),
            destName = uiState.destName,
            onDestChanged = { viewModel.updateDest(it) },
            sourceName = uiState.sourceName,
            itemId = uiState.destId,
            destErrorMessage = uiState.destErrorMessage,
            onClickDelete = { viewModel.updateDelConfDialogVisibility(true) },
            onNavigateUp = onNavigateUp,
            onClickSave = { viewModel.validate() },
            onClickSource = { viewModel.updateSourceListDialogVisibility(true) },
            onClickEditSource = onNavigateToSourceList,
        )

        if (uiState.showDelConfDialog) {
            DeleteConfirmDialog(
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

        if (uiState.showSourceListDialog) {
            if (uiState.sources.isNotEmpty()) {
                SourceListDialog(
                    items = uiState.sources,
                    onDismissRequest = { viewModel.updateSourceListDialogVisibility(false) },
                    onClickItem = { index ->
                        val source = uiState.sources[index]
                        viewModel.updateSource(
                            sourceId = source.id,
                            source = source.name
                        )
                    },
                    onClickEdit = {
                        viewModel.updateSourceListDialogVisibility(false)
                        viewModel.updateEditSourceListEventConsumed(false)
                    }
                )
            } else {
                NoSourceDataDialog(onDismissRequest = {
                    viewModel.updateSourceListDialogVisibility(
                        false
                    )
                })
            }
        } else {
            if (!uiState.editSourceListEventConsumed) {
                onNavigateToSourceList()
                viewModel.updateEditSourceListEventConsumed(true)
            }
        }
    }
}

@Composable
fun DestinationEditContents(
    modifier: Modifier = Modifier,
    destName: String,
    onDestChanged: (String) -> Unit,
    sourceName: String,
    itemId: Int,
    destErrorMessage: Int?,
    onClickDelete: () -> Unit,
    onNavigateUp: () -> Unit,
    onClickSave: () -> Unit,
    onClickSource: () -> Unit,
    onClickEditSource: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = destName,
            onValueChange = onDestChanged,
            label = { Text(stringResource(R.string.destination_text_label)) },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                if (destErrorMessage != null) Text(stringResource(destErrorMessage))
            },
            isError = destErrorMessage != null
        )

        SelectableText(
            sourceName = sourceName,
            onClickSource = { debouncedClick(onClickSource) },
            onClickEditSource = { debouncedClick(onClickEditSource) })

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

@Composable
fun SelectableText(
    sourceName: String,
    onClickSource: () -> Unit,
    onClickEditSource: () -> Unit,
) {
    SurfaceButton(
        onClick = {
            debouncedClick {
                onClickSource()
            }
        },
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .height(56.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                if (sourceName.isEmpty()) {
                    Text(
                        text = stringResource(R.string.source_text_label),
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.source_text_label),
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = sourceName,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 22.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.outline_edit_note_24),
                contentDescription = stringResource(id = R.string.edit_source_icon_description),
                modifier = Modifier
                    .size(ICON_EX_LARGE_SIZE)
                    .align(alignment = Alignment.CenterVertically)
                    .clickable(onClick = { debouncedClick(onClickEditSource) }),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewUpdateContents() {
    DestinationEditContents(
        destName = "横浜銀行クレジットカード",
        onDestChanged = {},
        sourceName = "横浜銀行",
        itemId = 1,
        destErrorMessage = null,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickEditSource = {},
    )
}

@Preview
@Composable
private fun PreviewRegisterContents() {
    DestinationEditContents(
        destName = "横浜銀行クレジットカード",
        onDestChanged = {},
        sourceName = "横浜銀行",
        itemId = 0,
        destErrorMessage = null,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickEditSource = {},
    )
}

@Preview
@Composable
private fun PreviewEmptyTextContents() {
    DestinationEditContents(
        destName = "",
        onDestChanged = {},
        sourceName = "",
        itemId = 0,
        destErrorMessage = null,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickEditSource = {},
    )
}

@Preview
@Composable
private fun PreviewValidationErrorContents() {
    DestinationEditContents(
        destName = "",
        onDestChanged = {},
        sourceName = "",
        itemId = 0,
        destErrorMessage = R.string.common_required_field,
        onClickDelete = {},
        onNavigateUp = {},
        onClickSave = {},
        onClickSource = {},
        onClickEditSource = {},
    )
}
