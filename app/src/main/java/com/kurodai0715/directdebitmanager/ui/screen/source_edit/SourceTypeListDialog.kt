package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.SourceType
import com.kurodai0715.directdebitmanager.ui.common_ui.DialogSurfaceButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


@Composable
fun SourceTypeListDialog(
    onDismissRequest: () -> Unit,
    onClickItem: (SourceType) -> Unit,
) {

    AlertDialog(
        title = {
            Text(text = stringResource(R.string.source_type_list_dialog_title))
        },
        text = {
            LazyColumn {
                items(SourceType.entries.toTypedArray()) { sourceType ->
                    DialogSurfaceButton(
                        onClick = {
                            debouncedClick {
                                onClickItem(sourceType)
                                onDismissRequest()
                            }
                        },
                    ) {
                        Text(
                            text = stringResource(getSourceTypeStringRes(sourceType)),
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
                text = stringResource(R.string.common_close),
                modifier = Modifier.clickable(onClick = { debouncedClick(onDismissRequest) }),
            )
        }
    )

}

@Preview
@Composable
private fun PreviewSourceTypeListDialog() {
    SourceTypeListDialog(
        onDismissRequest = {},
        onClickItem = {},
    )
}