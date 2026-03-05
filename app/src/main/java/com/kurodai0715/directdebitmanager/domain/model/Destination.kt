package com.kurodai0715.directdebitmanager.domain.model

sealed interface Destination {

    val label: String
    val isSourceItem: Boolean
    val parentId: Int

    data class New(
        override val label: String,
        override val isSourceItem: Boolean,
        override val parentId: Int,
    ) : Destination

    data class Existing(
        val id: Int,
        override val label: String,
        override val isSourceItem: Boolean,
        override val parentId: Int,
    ) : Destination

    companion object {
        fun from(
            destId: Int,
            label: String,
            isSourceItem: Boolean,
            parentId: Int
        ): Destination {
            return if (destId == 0) {
                New(label, isSourceItem, parentId)
            } else {
                Existing(destId, label, isSourceItem, parentId)
            }
        }
    }
}