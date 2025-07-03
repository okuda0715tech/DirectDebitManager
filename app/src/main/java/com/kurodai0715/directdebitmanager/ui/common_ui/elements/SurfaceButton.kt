package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SurfaceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = color,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun DialogSurfaceButton(
    modifier: Modifier = Modifier.Companion,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    SurfaceButton(
        onClick = onClick,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        content = content,
    )
}

@Preview
@Composable
private fun PreviewSurfaceButton() {
    SurfaceButton(
        onClick = {},
        content = {
            Text(
                text = "ボタン",
                modifier = Modifier.Companion.padding(12.dp)
            )
        }
    )
}