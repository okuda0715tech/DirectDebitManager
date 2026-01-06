package com.kurodai0715.directdebitmanager.domain.model

data class TransferInfo(

    val destId: Int,

    val destName: String = "",

    val inputType: DestInputType,

    val destAccountType: ItemType?,

    val sourceId: Int = 0,

    val sourceName: String = "",

    )