package com.kurodai0715.directdebitmanager.data.source

data class Source(
    /**
     * ID.
     *
     * ID を自動採番したい場合は 0 を設定してください。
     */
    val sourceId: Int = 0,

    val source: String
)