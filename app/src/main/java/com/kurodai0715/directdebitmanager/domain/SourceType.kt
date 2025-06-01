package com.kurodai0715.directdebitmanager.domain


enum class SourceType(val value: Int) {
    Bank(0),
    CreditCard(1),
    DebitCard(2),
    Others(3);

    companion object {
        fun fromInt(value: Int): SourceType {
            for (type in entries) {
                if (type.value == value) {
                    return type
                }
            }
            return Others
        }

        fun toInt(sourceType: SourceType): Int {
            for (type in entries) {
                if (type == sourceType) {
                    return type.value
                }
            }
            return Others.value
        }
    }

}