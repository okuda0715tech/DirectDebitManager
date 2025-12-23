package com.kurodai0715.directdebitmanager.ui.common_ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ContentsWithBottomButton(
    modifier: Modifier = Modifier,
    contents: @Composable () -> Unit,
    bottomButton: @Composable () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.weight(1f)) {
            contents()
        }

        HorizontalDivider()

        bottomButton()
    }
}

@Preview
@Composable
private fun ContentWithBottomButtonPreview() {
    ContentsWithBottomButton(
        contents = { Text("コンテンツ") },
        bottomButton = {
            Button(onClick = {}) {
                Text("ボタン")
            }
        }
    )
}
