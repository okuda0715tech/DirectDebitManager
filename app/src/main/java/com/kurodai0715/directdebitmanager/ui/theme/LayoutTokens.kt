package com.kurodai0715.directdebitmanager.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

object LayoutTokens {

    /**
     * アイコンとラベルの間、ラベルと入力欄の間など、一つのコンポーネント内の要素間の余白.
     */
    val elementSpacing: Dp
        @Composable get() = Spacing.s

    /**
     * コンポーネントとコンポーネントの間の余白.
     */
    val itemSpacing: Dp
        @Composable get() = Spacing.s

    /**
     * セクションとセクションの間の余白.
     */
    val sectionSpacing: Dp
        @Composable get() = Spacing.m

    /**
     * 画面と画面の間の余白.
     */
    val screenPadding: Dp
        @Composable get() = Spacing.l
}
