package com.kurodai0715.directdebitmanager.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.kurodai0715.directdebitmanager.ui.theme.LayoutTokens.itemSpacing

object LayoutTokens {

    /**
     * 一つのコンポーネント内の要素間の余白.
     *
     * 具体例 : アイコンとラベルの間、ラベルと入力欄の間など。
     */
    val elementSpacing: Dp
        @Composable get() = Spacing.s

    /**
     * コンポーネントとコンポーネントの間の余白.
     *
     * 具体例 : リストアイテムの要素間、フォームの項目同士の間など。
     */
    val itemSpacing: Dp
        @Composable get() = Spacing.s

    /**
     * [itemSpacing] の半分の余白.
     *
     * リストアイテムの要素間の余白は、隣り合う要素がそれぞれ余白を持つため、実際の余白の半分のプロパティがあると便利。
     */
    val itemSpacingHalf: Dp
        @Composable get() = itemSpacing / 2

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
