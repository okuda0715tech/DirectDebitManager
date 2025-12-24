/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens

@Composable
fun HorizontalTwoButton(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    leftText: String,
    rightText: String,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(LayoutTokens.sectionSpacing),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(onClick = { onClickLeft() }) {
            Text(leftText)
        }
        Button(onClick = { onClickRight() }) {
            Text(rightText)
        }
    }
}

@Preview
@Composable
private fun PreviewHorizontalTwoButton() {
    HorizontalTwoButton(
        onClickLeft = {},
        onClickRight = {},
        leftText = stringResource(R.string.common_back),
        rightText = stringResource(R.string.common_save)
    )
}
