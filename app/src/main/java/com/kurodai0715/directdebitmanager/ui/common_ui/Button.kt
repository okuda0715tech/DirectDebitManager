package com.kurodai0715.directdebitmanager.ui.common_ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R

@Composable
fun SurfaceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
fun HorizontalThreeButton(
    onClickLeft: () -> Unit,
    onClickCenter: () -> Unit,
    onClickRight: () -> Unit,
    leftText: String,
    centerText: String,
    rightText: String,
) {

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TextButton(onClick = { onClickLeft() }) {
            Text(
                text = leftText,
                color = MaterialTheme.colorScheme.error,
            )
        }
        OutlinedButton(onClick = { onClickCenter() }) {
            Text(centerText)
        }
        Button(onClick = { onClickRight() }) {
            Text(rightText)
        }
    }
}

@Preview
@Composable
private fun PreviewHorizontalThreeButton() {
    HorizontalThreeButton(
        onClickLeft = {},
        onClickCenter = {},
        onClickRight = {},
        leftText = stringResource(R.string.common_delete),
        centerText = stringResource(R.string.common_back),
        rightText = stringResource(R.string.common_save)
    )
}

@Composable
fun HorizontalTwoButton(
    onClickLeft: () -> Unit,
    onClickRight: () -> Unit,
    leftText: String,
    rightText: String,
) {

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
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

@Composable
fun OneButton(
    onClick: () -> Unit,
    text: String,
) {

    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
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
