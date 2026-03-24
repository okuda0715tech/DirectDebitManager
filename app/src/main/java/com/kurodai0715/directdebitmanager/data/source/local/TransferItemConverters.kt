package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.TypeConverter

/**
 * Room の Entity の Column を変換するためのクラス.
 */
class TransferItemConverters {

    @TypeConverter
    fun fromPaymentRole(role: PaymentRole): Int = role.value

    @TypeConverter
    fun toPaymentType(value: Int): PaymentRole =
        PaymentRole.fromValue(value)

}