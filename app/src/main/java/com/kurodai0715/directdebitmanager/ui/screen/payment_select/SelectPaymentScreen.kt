package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelectPaymentScreen(modifier: Modifier = Modifier) {
    SelectPaymentContents()
}

@Composable
fun SelectPaymentContents() {
    Text(text = "SelectPaymentContents")
}

@Preview(name = "SelectPaymentScreen")
@Composable
private fun Preview() {
    SelectPaymentContents()
}