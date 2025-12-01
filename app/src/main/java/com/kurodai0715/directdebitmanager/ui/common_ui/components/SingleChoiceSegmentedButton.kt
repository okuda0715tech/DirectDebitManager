package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.ui.theme.SPACE_EXTRA_SMALL

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    label: String,
    buttonLabels: List<String>,
    onSelected: (Int) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.Companion.size(SPACE_EXTRA_SMALL))

        SingleChoiceSegmentedButtonRow(modifier = modifier) {
            buttonLabels.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = buttonLabels.size
                    ),
                    onClick = {
                        onSelected(index)
                    },
                    selected = index == selectedIndex,
                    label = { Text(label) }
                )
            }
        }
    }
}

@Preview(name = "SingleChoiceSegmentedButton")
@Composable
private fun Preview() {
    SingleChoiceSegmentedButton(
        modifier = Modifier.width(400.dp),
        selectedIndex = 0,
        label = "単一選択セグメントボタン",
        buttonLabels = listOf("ボタンA", "ボタンB", "ボタンC"),
        onSelected = {},
    )
}
