package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.ICON_EX_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun SourceEditDeleteConfirmDialog(
    relatedDestCount: Int,
    onDismissRequest: () -> Unit,
    onClickNo: () -> Unit,
    onClickYes: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_outline_24),
                contentDescription = stringResource(id = R.string.del_conf_icon_description),
                modifier = Modifier.Companion.size(ICON_EX_LARGE_SIZE),
            )
        },
        title = {
            Text(text = stringResource(R.string.del_conf_title))
        },
        text = {
            if (relatedDestCount > 0) {
                Text(text = stringResource(R.string.del_conf_text_related_dest_exists, relatedDestCount))
            } else {
                Text(text = stringResource(R.string.del_conf_text))
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                debouncedClick {
                    onClickYes()
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                debouncedClick {
                    onClickNo()
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_no))
            }
        })
}

@Preview
@Composable
private fun PreviewDelConfDialogNoRelatedData() {
    SourceEditDeleteConfirmDialog(
        relatedDestCount = 0,
        onDismissRequest = {},
        onClickNo = {},
        onClickYes = {}
    )
}

@Preview
@Composable
private fun PreviewDelConfDialogExistRelatedData() {
    SourceEditDeleteConfirmDialog(
        relatedDestCount = 3,
        onDismissRequest = {},
        onClickNo = {},
        onClickYes = {}
    )
}
