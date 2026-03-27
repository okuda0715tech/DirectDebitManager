package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PaymentInfoEditScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentInfoEditViewModel = hiltViewModel()
) {
    TransferRelationEditContents(modifier)
}

@Composable
fun TransferRelationEditContents(modifier: Modifier = Modifier) {
    Contents(modifier)
}

@Composable
fun Contents(modifier: Modifier = Modifier) {
    Text(text = "Contents")
}

@Preview(name = "TransferRelationEditContents")
@Composable
private fun Preview() {
    TransferRelationEditContents()
}