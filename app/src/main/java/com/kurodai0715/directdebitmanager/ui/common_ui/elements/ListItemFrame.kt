package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

private const val TAG = "ListItemFrame"

@Composable
fun DefaultListItemFrame(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    label: String,
    onClickItem: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(vertical = LayoutTokens.itemSpacingHalf)
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerLow
                }
            )
            .clickable(onClick = {
                Log.v(TAG, "list item is clicked.")
                debouncedClick(onClickItem)
            })
            .padding(LayoutTokens.elementSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.weight(1f), text = label)

            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                    contentDescription = stringResource(id = R.string.selected_icon_description),
                    modifier = Modifier
                        .size(ICON_LARGE_SIZE)
                        .clickable(onClick = { debouncedClick(onClickItem) }),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
    }
}

@Preview(name = "DefaultListItemFrame")
@Composable
private fun SelectedPreview() {
    DefaultListItemFrame(
        label = "選択中のアイテム",
        isSelected = true,
        onClickItem = {}
    )
}

@Preview(name = "DefaultListItemFrame")
@Composable
private fun NonSelectedPreview() {
    DefaultListItemFrame(
        label = "選択されていないアイテム",
        isSelected = false,
        onClickItem = {}
    )
}