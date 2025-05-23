package com.kurodai0715.directdebitmanager.ui.destination_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.ui.common_ui.SurfaceButton
import com.kurodai0715.directdebitmanager.ui.theme.SCREEN_EDGE_PADDING_DEF
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceListDialog(
    modifier: Modifier = Modifier,
    items: List<Source>,
    onDismissRequest: () -> Unit,
    onClickItem: (Int) -> Unit,
    onClickEdit: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Column(horizontalAlignment = Alignment.Companion.CenterHorizontally) {
                LazyColumn(modifier = Modifier.Companion.padding(SCREEN_EDGE_PADDING_DEF)) {
                    itemsIndexed(items) { index, item ->

                        SurfaceButton(
                            onClick = {
                                debouncedClick {
                                    onClickItem(index)
                                    onDismissRequest()
                                }
                            },
                            modifier = Modifier.Companion.background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        ) {
                            Text(
                                text = item.name,
                                modifier = modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                    .padding(12.dp)
                            )
                        }

                        // 最後のアイテム以外なら分割線を引く
                        if (index != items.size - 1) {
                            HorizontalDivider()
                        }
                    }
                }

                HorizontalDivider()

                OutlinedButton(onClick = { debouncedClick(onClickEdit) }) {
                    Text(text = stringResource(R.string.common_edit))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSourceListDialog() {
    SourceListDialog(
        items = listOf(
            Source(id = 1, name = "横浜銀行"),
            Source(id = 2, name = "三井住友銀行"),
            Source(id = 3, name = "PayPay銀行"),
        ),
        onDismissRequest = {},
        onClickItem = {},
        onClickEdit = {},
    )
}
