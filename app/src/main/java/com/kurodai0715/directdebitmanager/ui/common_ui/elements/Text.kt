/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun DefaultText(
    text: String,
    onClickText: () -> Unit,
) {
    Box(
        modifier = Modifier
            .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
            .fillMaxWidth()
            .clickable(onClick = { debouncedClick(onClickText) })
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.CenterStart),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
private fun Preview() {
    DefaultText(
        text = "test",
        onClickText = { },
    )
}
