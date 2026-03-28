package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PaymentSelectScreen(
    viewModel: PaymentSelectViewModel = hiltViewModel(),
) {
    PaymentSelectContents()
}

@Composable
fun PaymentSelectContents() {
    Text(text = "PaymentSelectContents")
}

@Preview(name = "PaymentSelectContents")
@Composable
private fun Preview() {
    PaymentSelectContents()
}