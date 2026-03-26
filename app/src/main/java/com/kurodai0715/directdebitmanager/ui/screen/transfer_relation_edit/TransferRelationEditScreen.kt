package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TransferRelationEditScreen(modifier: Modifier = Modifier) {
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
