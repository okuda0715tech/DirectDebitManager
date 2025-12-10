/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.dialog.source_selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DialogSurfaceButton
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SPACE_SMALL
import com.kurodai0715.directdebitmanager.ui.theme.TAP_AREA_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceSelectionDialog(
    items: List<SourceSelectionUiModel>,
    onDismissRequest: () -> Unit,
    onClickItem: (SourceSelectionUiModel) -> Unit,
    onClickAddEdit: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.source_list_dialog_title))
        },
        text = {
            LazyColumn {
                items(items) { item ->
                    DialogSurfaceButton(
                        onClick = {
                            debouncedClick {
                                onClickItem(item)
                                onDismissRequest()
                            }
                        },
                    ) {
                        Text(
                            text = item.sourceName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Row(
                modifier = Modifier
                    .height(TAP_AREA_DEF)
                    .clickable(onClick = { debouncedClick(onClickAddEdit) }),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_edit_24),
                    contentDescription = stringResource(R.string.open_source_list_dialog_icon_description),
                    modifier = Modifier.size(ICON_LARGE_SIZE),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.size(SPACE_SMALL))
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.edit_here),
                    )
                }
            }
        }
    )
}

/**
 * 何を選択するために振替元一覧ダイアログを表示したのかを区別するための enum.
 */
enum class SourceListDialogType() {
    Source, // 振替元を選択するため
    Destination; // 振替先を選択するため
}

@Preview
@Composable
private fun PreviewSourceListDialog() {
    SourceSelectionDialog(
        items = listOf(
            SourceSelectionUiModel(sourceId = 1, sourceName = "横浜銀行"),
            SourceSelectionUiModel(sourceId = 2, sourceName = "三井住友銀行"),
            SourceSelectionUiModel(sourceId = 3, sourceName = "PayPay銀行"),
        ),
        onDismissRequest = {},
        onClickItem = {},
        onClickAddEdit = {},
    )
}
