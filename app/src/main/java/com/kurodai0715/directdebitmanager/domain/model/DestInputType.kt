/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.model

enum class DestInputType(val value: Int) {
    Keyboard(0),
    SourceList(1);

    companion object {
        fun fromInt(value: Int): DestInputType {
            for (type in entries) {
                if (type.value == value) {
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
                .sortedBy { it.value } // value の昇順にソート
        }
    }

}