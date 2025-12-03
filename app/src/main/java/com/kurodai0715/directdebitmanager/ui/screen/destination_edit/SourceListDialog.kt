package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

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
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DialogSurfaceButton
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SPACE_SMALL
import com.kurodai0715.directdebitmanager.ui.theme.TAP_AREA_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


// TODO UI はドメインモデルに依存するべきではないため、修正が必要。
//  UI が依存しても良いのは、 UI 状態のみで、それ以外の場合は、プリミティブ型でデータを扱う。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceListDialog(
    items: List<Source>,
    onDismissRequest: () -> Unit,
    onClickItem: (Int) -> Unit,
    onClickAddEdit: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.source_list_dialog_title))
        },
        text = {
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    DialogSurfaceButton(
                        onClick = {
                            debouncedClick {
                                onClickItem(index)
                                onDismissRequest()
                            }
                        },
                    ) {
                        Text(
                            text = item.name,
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
    SourceListDialog(
        items = listOf(
            Source(id = 1, name = "横浜銀行", type = 0, typeEnum = TransferItemType.Bank),
            Source(id = 2, name = "三井住友銀行", type = 0, typeEnum = TransferItemType.Bank),
            Source(id = 3, name = "PayPay銀行", type = 0, typeEnum = TransferItemType.Bank),
        ),
        onDismissRequest = {},
        onClickItem = {},
        onClickAddEdit = {},
    )
}
