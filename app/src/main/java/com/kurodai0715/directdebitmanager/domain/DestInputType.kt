package com.kurodai0715.directdebitmanager.domain

enum class DestInputType(val value: Int) {
    Keyboard(0),
    SourceList(1);

    companion object {
        fun fromInt(value: Int): DestInputType {
            for (type in DestInputType.entries) {
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

        fun toInt(destInputType: DestInputType): Int {
            for (type in DestInputType.entries) {
                if (type == destInputType) {
                    return type.value
                }
            }

            try {
                throw IllegalArgumentException()
            } catch (e: IllegalArgumentException) {
                // TODO エラーハンドリング
            }

            return Keyboard.value
        }

        fun getList(): List<DestInputType> {
            return DestInputType.entries.toList()
                .sortedBy { it.value } // value の昇順にソート
        }
    }

}
