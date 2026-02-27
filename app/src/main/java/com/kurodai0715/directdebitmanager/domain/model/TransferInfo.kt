package com.kurodai0715.directdebitmanager.domain.model

data class TransferInfo(

    val destId: Int,

    val destName: String = "",

    val inputType: DestInputType,

    val sourceId: Int = 0,

    val sourceName: String = "",

    )