/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultBasicTextField


@Composable
fun EditableFormField(
    labelText: String,
    text: String,
    onTextChanged: (String) -> Unit,
    supportingText: Int?,
    onClickClear: () -> Unit = {},
) {
    BaseFormField(
        labelText = labelText,
        supportingText = supportingText,
        onClickIcon = onClickClear,
        icon = if(text.isEmpty()) null else painterResource(id = R.drawable.cancel_24px),
        iconDescription = stringResource(id = R.string.clear_text_icon_description),
        userInputComposable = {
            DefaultBasicTextField(
                text = text,
                onTextChanged = onTextChanged,
            )
        }
    )
}

@Preview
@Composable
private fun PreviewFilled() {
    EditableFormField(
        labelText = stringResource(R.string.destination_text_label),
        text = "横浜銀行",
        onTextChanged = { },
        supportingText = null,
        onClickClear = { })
}

@Preview
@Composable
private fun PreviewEmpty() {
    EditableFormField(
        labelText = stringResource(R.string.destination_text_label),
        text = "",
        onTextChanged = { },
        supportingText = R.string.common_required_field,
        onClickClear = { })
}

