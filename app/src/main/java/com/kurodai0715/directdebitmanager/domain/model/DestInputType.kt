/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.model

enum class DestInputType(val defaultDisplayOrder: Int) {
    Keyboard(0),
    SourceList(1);

    companion object {
        fun fromInt(displayOrder: Int): DestInputType {
            for (type in entries) {
                if (type.defaultDisplayOrder == displayOrder) {
                    return type
                }
            }

            try {
                throw IllegalArgumentException()
            } catch (e: IllegalArgumentException) {
                // TODO エラーハンドリング
            }

            return Keyboard
        }

        fun getSortedList(): List<DestInputType> {
            return entries.toList()
                .sortedBy { it.defaultDisplayOrder } // value の昇順にソート
        }
    }

}