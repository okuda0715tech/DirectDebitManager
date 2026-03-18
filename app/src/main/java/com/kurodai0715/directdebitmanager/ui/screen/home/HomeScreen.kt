package com.kurodai0715.directdebitmanager.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kurodai0715.directdebitmanager.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    HomeScreenContents(modifier)
}

@Composable
fun HomeScreenContents(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = {}) {
            Text(text = stringResource(R.string.payer_list_label))
        }
        Button(onClick = {}) {
            Text(text = stringResource(R.string.payee_list_label))
        }
        Button(onClick = {}) {
            Text(text = stringResource(R.string.relation_list_label))
        }
    }
}

@Preview
@Composable
private fun PreviewHomeScreenContents() {
    HomeScreenContents()
}