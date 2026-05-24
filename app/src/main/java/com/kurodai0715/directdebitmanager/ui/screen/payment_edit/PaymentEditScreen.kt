package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentEditScreen() {
    PaymentEditContents(
        onClickBack = { TODO() },
        onClickSave = { TODO() },
    )
}

@Composable
fun PaymentEditContents(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
    onClickSave: () -> Unit,
) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents()
        },
        bottomButton = {
            BottomButton(onClickBack, onClickSave)
        }
    )
}

@Composable
fun Contents() {
    Text("PaymentEditScreen")
}

@Composable
private fun BottomButton(onClickBack: () -> Unit, onClickSave: () -> Unit) {
    HorizontalTwoButton(
        onClickLeft = { debouncedClick(onClickBack) },
        onClickRight = { debouncedClick(onClickSave) },
        leftText = stringResource(R.string.common_back),
        rightText = stringResource(R.string.common_save)
    )
}