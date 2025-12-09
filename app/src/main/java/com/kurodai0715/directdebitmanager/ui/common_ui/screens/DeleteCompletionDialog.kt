/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.screens

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
fun DeleteCompletionDialog(
    onClickClose: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                contentDescription = stringResource(id = R.string.del_comp_icon_description),
                modifier = Modifier.size(ICON_EX_LARGE_SIZE),
            )
        },
        title = {
            Text(text = stringResource(R.string.del_comp_title))
        },
        text = {
            Text(text = stringResource(R.string.del_comp_text))
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = { debouncedClick(onClickClose) }) {
                Text(stringResource(R.string.common_close))
            }
        })
}

@Preview
@Composable
private fun PreviewContents() {
    DeleteCompletionDialog(
        onClickClose = {},
    )
}