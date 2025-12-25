/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens
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
    userInputComposable: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = LayoutTokens.itemSpacing)
        )

        Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = LayoutTokens.itemSpacing)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                userInputComposable()
            }

            if (iconVisible && icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = iconDescription,
                    modifier = Modifier
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

        Spacer(modifier = Modifier.size(LayoutTokens.elementSpacing))

        Text(
            text = if (supportingText != null) stringResource(supportingText) else "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = LayoutTokens.itemSpacing)
        )
    }
}

@Preview
@Composable
private fun PreviewNormal() {
    AppBaseFormField(
        labelText = "test",
        supportingText = null,
        iconVisible = true,
        icon = ColorPainter(Color.Gray),
        iconDescription = null,
        userInputComposable = {
            Text(text = "test")
        }
    )
}

@Preview
@Composable
private fun PreviewError() {
    AppBaseFormField(
        labelText = "test",
        // TODO コンポーザブル関数の引数でリソースそのものを渡すと不要な依存が発生するため、やめたい。
        supportingText = R.string.common_required_field,
        iconVisible = true,
        icon = null,
        iconDescription = null,
        userInputComposable = {
            Text(text = "test")
        }
    )
}