package com.kurodai0715.directdebitmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kurodai0715.directdebitmanager.ui.app_base.AppBaseScreen
import com.kurodai0715.directdebitmanager.ui.theme.DirectDebitManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DirectDebitManagerTheme {
                AppBaseScreen()
            }
        }
    }
}
