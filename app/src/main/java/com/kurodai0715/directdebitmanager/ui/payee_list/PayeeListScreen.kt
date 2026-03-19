package com.kurodai0715.directdebitmanager.ui.payee_list

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PayeeListScreen(modifier: Modifier = Modifier) {
    PayeeListContents(modifier)
}

@Composable
fun PayeeListContents(modifier: Modifier = Modifier) {
    Text(text = "payee list", modifier = modifier)
}

@Preview
@Composable
private fun PreviewPayeeListContents() {
    PayeeListContents()
}