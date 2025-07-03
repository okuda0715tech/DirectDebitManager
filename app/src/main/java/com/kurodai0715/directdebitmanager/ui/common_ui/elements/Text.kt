package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun DefaultText(
    modifier: Modifier,
    text: String,
    onClickText: () -> Unit,
) {
    Box(
        modifier = modifier
            .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
            .clickable(onClick = { debouncedClick(onClickText) })
    ) {
        Text(
            text = text,
            modifier = Modifier.Companion.align(Alignment.Companion.CenterStart),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textAlign = TextAlign.Companion.Start
        )
    }
}