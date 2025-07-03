package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R

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


