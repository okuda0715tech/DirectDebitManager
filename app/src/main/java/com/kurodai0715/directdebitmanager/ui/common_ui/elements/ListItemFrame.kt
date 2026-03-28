package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "ListItemFrame"

@Composable
fun DefaultListItemFrame(
    modifier: Modifier = Modifier,
    onClickItem: () -> Unit,
    contents: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .padding(vertical = LayoutTokens.itemSpacingHalf)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable(onClick = {
                Log.v(TAG, "list item is clicked.")
                debouncedClick(onClickItem)
            })
            .padding(LayoutTokens.elementSpacing)
    ) {
        contents()
    }
}

@Preview
@Composable
private fun Preview() {
    DefaultListItemFrame(onClickItem = {}) {
        Text(text = "アイテム")
    }
}