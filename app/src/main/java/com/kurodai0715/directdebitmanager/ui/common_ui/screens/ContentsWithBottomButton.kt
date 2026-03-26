package com.kurodai0715.directdebitmanager.ui.common_ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens

@Composable
fun ContentsWithBottomButton(
    modifier: Modifier = Modifier,
    contents: @Composable () -> Unit,
    bottomButton: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(LayoutTokens.screenPaddingHalf),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
