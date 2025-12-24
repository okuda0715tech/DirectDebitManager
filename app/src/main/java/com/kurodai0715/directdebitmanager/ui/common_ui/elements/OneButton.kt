/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens

@Composable
fun OneButton(
    onClick: () -> Unit,
    text: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LayoutTokens.sectionSpacing),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onClick() }) {
            Text(text)
        }
    }
}

@Preview
@Composable
private fun PreviewOneButton() {
    OneButton(
        onClick = {},
        text = stringResource(R.string.common_save)
    )
}