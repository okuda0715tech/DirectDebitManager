package com.kurodai0715.directdebitmanager.ui.common_ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
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
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick


@Composable
fun KeyboardEditableFormField(
    labelText: String,
    text: String,
    onTextChanged: (String) -> Unit,
    supportingText: Int?,
    onClickClear: () -> Unit = {},
) {
    AppBaseFormField(
        labelText = labelText,
        supportingText = supportingText,
        onClickIcon = onClickClear,
        iconVisible = !text.isEmpty(),
        icon = painterResource(id = R.drawable.cancel_24px),
        iconDescription = stringResource(id = R.string.clear_text_icon_description),
        userInputComposable = { modifier ->
            DefaultBasicTextField(
                modifier = modifier,
                text = text,
                onTextChanged = onTextChanged,
            )
        }
    )
}

@Composable
fun DefaultBasicTextField(
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
private fun PreviewKeyboardEditableFormFieldFilled() {
    KeyboardEditableFormField(
        labelText = stringResource(R.string.destination_text_label),
        text = "横浜銀行",
        onTextChanged = { },
        supportingText = null,
        onClickClear = { })
}

@Preview
@Composable
private fun PreviewKeyboardEditableFormFieldEmpty() {
    KeyboardEditableFormField(
        labelText = stringResource(R.string.destination_text_label),
        text = "",
        onTextChanged = { },
        supportingText = R.string.common_required_field,
        onClickClear = { })
}

@Composable
fun DisplayTextFormField(
    labelText: String,
    text: String,
    onClickText: () -> Unit,
    supportingText: Int?,
    icon: Painter? = null,
    iconDescription: String? = null,
    onClickIcon: (() -> Unit) = {},
) {
    AppBaseFormField(
        labelText = labelText,
        supportingText = supportingText,
        onClickIcon = onClickIcon,
        iconVisible = true,
        icon = icon,
        iconDescription = iconDescription,
        userInputComposable = { modifier ->
            DefaultText(
                modifier = modifier,
                text = text,
                onClickText = onClickText,
            )
        }
    )
}

@Composable
fun DefaultText(
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
private fun PreviewDisplayTextFormFieldFilled() {
    DisplayTextFormField(
        labelText = stringResource(R.string.source_text_label),
        text = "横浜銀行",
        onClickText = { },
        onClickIcon = { },
        supportingText = null,
    )
}

@Preview
@Composable
private fun PreviewDisplayTextFormFieldEmpty() {
    DisplayTextFormField(
        labelText = stringResource(R.string.source_text_label),
        text = "",
        onClickText = { },
        onClickIcon = { },
        supportingText = R.string.common_required_field,
    )
}
