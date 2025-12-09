/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R

@Composable
fun OneOutlinedButton(
    onClick: () -> Unit,
    text: String,
) {

    Row(
        modifier = Modifier.Companion.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        OutlinedButton(onClick = { onClick() }) {
            Text(text)
        }
    }
}

@Preview
@Composable
private fun PreviewOneOutlinedButton() {
    OneOutlinedButton(
        onClick = {},
        text = stringResource(R.string.common_edit)
    )
}