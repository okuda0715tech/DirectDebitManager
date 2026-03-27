package com.kurodai0715.directdebitmanager.ui.screen.payment_info_edit

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.common_ui.components.HorizontalTwoButton
import com.kurodai0715.directdebitmanager.ui.common_ui.screens.ContentsWithBottomButton
import com.kurodai0715.directdebitmanager.ui.util.debouncedClick

@Composable
fun PaymentInfoEditScreen(
    viewModel: PaymentInfoEditViewModel = hiltViewModel()
) {
    TransferRelationEditContents()
}

@Composable
fun TransferRelationEditContents(modifier: Modifier = Modifier) {
    ContentsWithBottomButton(
        modifier = modifier,
        contents = {
            Contents()
        },
        bottomButton = {
            HorizontalTwoButton(
                onClickLeft = { debouncedClick(TODO()) },
                onClickRight = { debouncedClick(TODO()) },
                leftText = stringResource(R.string.common_back),
                rightText = stringResource(R.string.common_save)
            )
        }
    )

}

@Composable
fun Contents() {
    Text(text = "Contents")
}

@Preview(name = "TransferRelationEditContents")
@Composable
private fun Preview() {
    TransferRelationEditContents()
}