/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.common_ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.DefaultText

@Composable
fun ReadOnlyForm(
    labelText: String,
    text: String,
    onClickText: () -> Unit,
    supportingText: Int?,
    icon: Painter,
    iconDescription: String? = null,
    onClickIcon: (() -> Unit) = {},
) {
    BaseFormField(
        labelText = labelText,
        supportingText = supportingText,
        onClickIcon = onClickIcon,
        icon = icon,
        iconDescription = iconDescription,
        userInputComposable = {
            // TODO あまり見ない関数呼び出しの形になっていて、なんか気持ち悪い。書き直したい。
            //  もしかしたら、このコンポーザブルのプレビューはもうひとつ中で行うべき？
            DefaultText(
                text = text,
                onClickText = onClickText,
            )
        }
    )
}

@Preview
@Composable
private fun PreviewFilled() {
    ReadOnlyForm(
        labelText = stringResource(R.string.source_text_label),
        text = "横浜銀行",
        onClickText = { },
        onClickIcon = { },
        supportingText = null,
        icon = painterResource(R.drawable.outline_arrow_drop_down_circle_24),
    )
}

@Preview
@Composable
private fun PreviewEmpty() {
    ReadOnlyForm(
        labelText = stringResource(R.string.source_text_label),
        text = "",
        onClickText = { },
        onClickIcon = { },
        supportingText = R.string.common_required_field,
        icon = painterResource(R.drawable.outline_arrow_drop_down_circle_24),
    )
}