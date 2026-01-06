/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.model

// TODO このクラスがいろいろなところから参照されすぎなので、分けた方がよさそう。
//  ただし、このクラスはドメインルールそのものなので、ある程度までは仕方がない部分もある。
enum class ItemType(val value: Int) {
    Bank(0),
    CreditCard(1),
    DebitCard(2),
    Others(3);

    companion object {
        fun fromInt(value: Int): ItemType =
            entries.firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("Unexpected value: $value")
    }

}