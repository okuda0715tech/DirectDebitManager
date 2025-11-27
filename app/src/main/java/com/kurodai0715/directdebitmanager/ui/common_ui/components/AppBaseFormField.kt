package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SPACE_EXTRA_SMALL
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun AppBaseFormField(
    labelText: String,
    supportingText: Int?,
    onClickIcon: () -> Unit = {},
    iconVisible: Boolean,
    icon: Painter?,
    iconDescription: String?,
    userInputComposable: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = Modifier.Companion.fillMaxWidth()
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.Companion.size(SPACE_EXTRA_SMALL))

        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            modifier = Modifier.Companion
                .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(start = 16.dp, end = 12.dp)
        ) {
            userInputComposable(Modifier.Companion.weight(1f))

            if (iconVisible && icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = iconDescription,
                    modifier = Modifier.Companion
                        .size(ICON_LARGE_SIZE)
                        .clickable(onClick = { debouncedClick(onClickIcon) }),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        HorizontalDivider(
            color = if (supportingText == null)
                MaterialTheme.colorScheme.onSurfaceVariant
            else
                MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.Companion.size(SPACE_EXTRA_SMALL))

        Text(
            text = if (supportingText != null) stringResource(supportingText) else "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
        )
    }
}