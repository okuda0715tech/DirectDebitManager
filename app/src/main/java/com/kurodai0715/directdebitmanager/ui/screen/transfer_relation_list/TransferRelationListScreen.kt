package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun TransferRelationListScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAdd: () -> Unit,
) {
    TransferRelationListContents(
        modifier,
        onClickBack,
        onClickAdd,
    )
}

@Composable
fun TransferRelationListContents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickAdd: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents()
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(onClickBack) },
                onClickRight = { debouncedClick(onClickAdd) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_add)
            )
        }
    )
}

@Composable
fun Contents() {
    Text(text = "Contents")
}