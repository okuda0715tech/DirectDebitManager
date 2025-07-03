package com.kurodai0715.directdebitmanager.ui.common_ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R


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
