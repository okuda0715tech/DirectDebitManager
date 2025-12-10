/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.model

enum class TransferItemType(val value: Int) {
    Bank(0),
    CreditCard(1),
    DebitCard(2),
    Others(3);

    companion object {
        fun fromInt(value: Int): TransferItemType {
            for (type in entries) {
                if (type.value == value) {
                    return type
                }
            }
            return Others
        }

        fun toInt(transferItemType: TransferItemType): Int {
            for (type in entries) {
                if (type == transferItemType) {
                    return type.value
                }
            }
            return Others.value
        }
    }

}