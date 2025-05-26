package com.kurodai0715.directdebitmanager.ui.common_ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun AppTextField(
    labelText: String,
    text: String,
    onTextChanged: (String) -> Unit,
    supportingText: Int?,
    onClickClear: () -> Unit = {}
) {
    Column(
        modifier = Modifier.Companion.fillMaxWidth()
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
        )
        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            modifier = Modifier.Companion
                .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(start = 16.dp, end = 12.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChanged,
                textStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                modifier = Modifier.Companion.weight(1f)
            )
            if (!text.isEmpty()) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel_24px),
                    contentDescription = stringResource(id = R.string.edit_source_icon_description),
                    modifier = Modifier.Companion
                        .size(ICON_LARGE_SIZE)
                        .clickable(onClick = { debouncedClick(onClickClear) }),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        var dividerColor = MaterialTheme.colorScheme.onSurfaceVariant
        if (supportingText != null) {
            dividerColor = MaterialTheme.colorScheme.error
        }
        HorizontalDivider(
            color = if (supportingText == null)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.error
        )
        if (supportingText != null) {
            Text(
                text = stringResource(supportingText),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewAppTextFieldFilled() {
    AppTextField(
        labelText = stringResource(R.string.destination_text_label),
        text = "横浜銀行",
        onTextChanged = { },
        supportingText = null,
        onClickClear = { })
}

@Preview
@Composable
private fun PreviewAppTextFieldEmpty() {
    AppTextField(
        labelText = stringResource(R.string.destination_text_label),
        text = "",
        onTextChanged = { },
        supportingText = R.string.common_required_field,
        onClickClear = { })
}
