package com.kurodai0715.directdebitmanager.ui.common_ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.ui.theme.TEXT_FIELD_MIN_HEIGHT

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
                modifier = Modifier.Companion.fillMaxWidth(),
                contentAlignment = Alignment.Companion.CenterStart // 左寄せ & 垂直中央
            ) {
                innerTextField()
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    DefaultBasicTextField(
        modifier = Modifier,
        text = "test",
        onTextChanged = { },
    )
}