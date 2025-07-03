package com.kurodai0715.directdebitmanager.ui.common_ui.screens

import androidx.annotation.StringRes
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
fun DeleteNotAllowedDialog(
    @StringRes messageResId: Int,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_block_24),
                contentDescription = stringResource(id = R.string.del_not_allowed_icon_description),
                modifier = Modifier.Companion.size(ICON_EX_LARGE_SIZE),
            )
        },
        title = {
            Text(text = stringResource(R.string.del_not_allowed_title))
        },
        text = {
            Text(text = stringResource(messageResId))
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                debouncedClick {
                    onDismissRequest()
                }
            }) {
                Text(stringResource(R.string.common_close))
            }
        },
    )
}

@Preview
@Composable
private fun PreviewDelNotAllowedDialogInSourceEdit() {
    DeleteNotAllowedDialog(
        messageResId = R.string.del_not_allowed_text_in_source_edit,
        onDismissRequest = {},
    )
}

@Preview
@Composable
private fun PreviewDelNotAllowedDialogInTransferEdit() {
    DeleteNotAllowedDialog(
        messageResId = R.string.del_not_allowed_text_in_transfer_edit,
        onDismissRequest = {},
    )
}