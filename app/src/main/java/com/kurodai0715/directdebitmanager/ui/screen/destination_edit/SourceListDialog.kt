package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.ui.common_ui.DialogSurfaceButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourceListDialog(
    items: List<Source>,
    onDismissRequest: () -> Unit,
    onClickItem: (Int) -> Unit,
    onClickEdit: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.source_select_dialog_title))
        },
        text = {
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    DialogSurfaceButton (
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
            Text(
                text = stringResource(R.string.common_edit),
                modifier = Modifier.clickable(onClick = { debouncedClick(onClickEdit) }),
            )
        }
    )
}

@Preview
@Composable
private fun PreviewSourceListDialog() {
    SourceListDialog(
        items = listOf(
            Source(id = 1, name = "横浜銀行", type = 0),
            Source(id = 2, name = "三井住友銀行", type = 0),
            Source(id = 3, name = "PayPay銀行", type = 0),
        ),
        onDismissRequest = {},
        onClickItem = {},
        onClickEdit = {},
    )
}
