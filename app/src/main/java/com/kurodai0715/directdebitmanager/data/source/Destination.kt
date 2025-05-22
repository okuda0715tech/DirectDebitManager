package com.kurodai0715.directdebitmanager.data.source

data class Destination(
    /**
     * ID.
     *
     * ID を自動採番したい場合は 0 を設定してください。
     */
    val id: Int = 0,

    val name: String,

    val sourceName: String,
//    val date: String,
//    val amount: String,
)