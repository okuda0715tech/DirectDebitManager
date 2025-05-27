package com.kurodai0715.directdebitmanager.data.source

data class Source(
    /**
     * ID.
     *
     * ID を自動採番したい場合は 0 を設定してください。
     */
    val id: Int = 0,

    val name: String,

    val type: Int,
)

sealed class SourceType {
    object Bank : SourceType()
    object CreditCard : SourceType()
    object DebitCard : SourceType()
    object Others : SourceType()

    companion object {
        fun fromInt(value: Int): SourceType = when (value) {
            0 -> Bank
            1 -> CreditCard
            2 -> DebitCard
            else -> Others
        }

        fun toInt(sourceType: SourceType): Int = when (sourceType) {
            Bank -> 0
            CreditCard -> 1
            DebitCard -> 2
            Others -> 3
        }
    }
}
