/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultText

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
            // TODO あまり見ない関数呼び出しの形になっていて、なんか気持ち悪い。書き直したい。
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