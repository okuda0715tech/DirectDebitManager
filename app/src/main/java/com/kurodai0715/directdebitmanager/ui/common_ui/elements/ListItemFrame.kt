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

sealed interface ItemState {
    data object None : ItemState
    data object Selected : ItemState
    data object Registered : ItemState
}

@Composable
fun ListItemFrame(
    modifier: Modifier = Modifier,
    itemState: ItemState = ItemState.None,
    label: String,
    onClickItem: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(vertical = LayoutTokens.itemSpacingHalf)
            .fillMaxWidth()
            .background(
                when (itemState) {
                    ItemState.Selected ->
                        MaterialTheme.colorScheme.secondaryContainer

                    ItemState.Registered ->
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)

                    ItemState.None ->
                        MaterialTheme.colorScheme.surfaceContainerLow
                }
            )
            .clickable(onClick = {
                Log.v(TAG, "list item is clicked.")
                when (itemState) {
                    ItemState.Registered -> {
                        // クリック処理を行わない
                    }

                    else -> debouncedClick(onClickItem)
                }
            })
            .padding(LayoutTokens.elementSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.weight(1f), text = label)

            when (itemState) {
                ItemState.Selected ->
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                        contentDescription = stringResource(id = R.string.selected_icon_description),
                        modifier = Modifier
                            .size(ICON_LARGE_SIZE)
                            .clickable(onClick = { debouncedClick(onClickItem) }),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )

                ItemState.Registered ->
                    Text(text = stringResource(id = R.string.registered_label))

                ItemState.None -> {
                    // 何も表示しない
                }
            }
        }
    }
}

@Preview(name = "ListItemFrame")
@Composable
private fun SelectedPreview() {
    ListItemFrame(
        label = "選択中のアイテム",
        itemState = ItemState.Selected,
        onClickItem = {}
    )
}

@Preview(name = "ListItemFrame")
@Composable
private fun NonSelectedPreview() {
    ListItemFrame(
        label = "通常状態アイテム",
        itemState = ItemState.None,
        onClickItem = {}
    )
}

@Preview(name = "ListItemFrame")
@Composable
private fun RegisteredPreview() {
    ListItemFrame(
        label = "登録済みのアイテム",
        itemState = ItemState.Registered,
        onClickItem = {}
    )
}