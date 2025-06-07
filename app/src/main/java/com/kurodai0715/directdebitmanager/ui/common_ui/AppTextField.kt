package com.kurodai0715.directdebitmanager.ui.common_ui

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.ICON_LARGE_SIZE
import com.kurodai0715.directdebitmanager.ui.theme.SPACE_EXTRA_SMALL
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


@Composable
fun AppBaseText(
    labelText: String,
    supportingText: Int?,
    onClickIcon: () -> Unit = {},
    iconVisible: Boolean,
    icon: Painter?,
    iconDescription: String?,
    textComposable: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = Modifier.Companion.fillMaxWidth()
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
        )

        Spacer(modifier = Modifier.size(SPACE_EXTRA_SMALL))

        Row(
            verticalAlignment = Alignment.Companion.CenterVertically,
            modifier = Modifier.Companion
                .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
                .background(color = MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(start = 16.dp, end = 12.dp)
        ) {
            textComposable(Modifier.weight(1f))

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

        Spacer(modifier = Modifier.size(SPACE_EXTRA_SMALL))

        Text(
            text = if (supportingText != null) stringResource(supportingText) else "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.Companion.padding(start = 16.dp, end = 16.dp)
        )
    }
}

@Composable
fun AppTextField(
    labelText: String,
    text: String,
    onTextChanged: (String) -> Unit,
    supportingText: Int?,
    onClickClear: () -> Unit = {},
) {
    AppBaseText(
        labelText = labelText,
        supportingText = supportingText,
        onClickIcon = onClickClear,
        iconVisible = !text.isEmpty(),
        icon = painterResource(id = R.drawable.cancel_24px),
        iconDescription = stringResource(id = R.string.clear_text_icon_description),
        textComposable = { modifier ->
            AppDefaultBasicTextField(
                modifier = modifier,
                text = text,
                onTextChanged = onTextChanged,
            )
        }
    )
}

@Composable
fun AppDefaultBasicTextField(
    modifier: Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChanged,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        ),
        modifier = modifier.heightIn(min = TEXT_FIELD_MIN_HEIGHT),
        decorationBox = { innerTextField ->
            // 中央に配置するために Box でラップ
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart // 左寄せ & 垂直中央
            ) {
                innerTextField()
            }
        }
    )
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

@Composable
fun SelectableText(
    labelText: String,
    text: String,
    onClickText: () -> Unit,
    supportingText: Int?,
    icon: Painter? = null,
    iconDescription: String? = null,
    onClickIcon: (() -> Unit) = {},
) {
    AppBaseText(
        labelText = labelText,
        supportingText = supportingText,
        onClickIcon = onClickIcon,
        iconVisible = true,
        icon = icon,
        iconDescription = iconDescription,
        textComposable = { modifier ->
            AppDefaultText(
                modifier = modifier,
                text = text,
                onClickText = onClickText,
            )
        }
    )
}

@Composable
fun AppDefaultText(
    modifier: Modifier,
    text: String,
    onClickText: () -> Unit,
) {
    Box(
        modifier = modifier
            .heightIn(min = TEXT_FIELD_MIN_HEIGHT)
            .clickable(onClick = { debouncedClick(onClickText) })
    ) {
        Text(
            text = text,
            modifier = Modifier.align(Alignment.CenterStart),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
private fun PreviewSelectableTextFilled() {
    SelectableText(
        labelText = stringResource(R.string.source_text_label),
        text = "横浜銀行",
        onClickText = { },
        onClickIcon = { },
        supportingText = null,
    )
}

@Preview
@Composable
private fun PreviewSelectableTextEmpty() {
    SelectableText(
        labelText = stringResource(R.string.source_text_label),
        text = "",
        onClickText = { },
        onClickIcon = { },
        supportingText = R.string.common_required_field,
    )
}
