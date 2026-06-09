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
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onClickPayerList: () -> Unit,
    onClickPayeeList: () -> Unit,
    onClickRelationList: () -> Unit,
) {
    HomeScreenContents(
        modifier,
        onClickPayerList,
        onClickPayeeList,
        onClickRelationList
    )
}

@Composable
fun HomeScreenContents(
    modifier: Modifier = Modifier,
    onClickPayerList: () -> Unit,
    onClickPayeeList: () -> Unit,
    onClickRelationList: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = { debouncedClick { onClickRelationList() } }) {
            Text(text = stringResource(R.string.relation_list_label))
        }
    }
}

@Preview
@Composable
private fun PreviewHomeScreenContents() {
    HomeScreenContents(
        onClickPayerList = {},
        onClickPayeeList = {},
        onClickRelationList = {},
    )
}