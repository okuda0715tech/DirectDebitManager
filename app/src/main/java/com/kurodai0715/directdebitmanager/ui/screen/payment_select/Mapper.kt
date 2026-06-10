package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntity

/**
 * エンティティを UI モデルに変換する(個別用).
 *
 * @param editingPaymentId 編集中の支払情報 ID
 * @param registeredPayerId 登録済みの支払元 ID
 */
fun PaymentEntity.toPaymentSelectUiModel(
    editingPaymentId: Int,
): PaymentSelectUiState.Success.Item {
    return PaymentSelectUiState.Success.Item(
        id = id,
        name = label,
        state = isRegistered(editingPaymentId)
    )
}

/**
 * 現在編集中の支払情報に対する「支払元」あるいは「支払先」に既に登録されているか、されていないかのステータスを返す.
 */
private fun PaymentEntity.isRegistered(editingPaymentId: Int): ItemState {
    return if (editingPaymentId == parentId) { // 支払先として登録されている場合
        ItemState.Registered
    } else {
        ItemState.None
    }
}

/**
 * エンティティを UI モデルに変換する(リスト用)、かつ、自分自身の支払情報は除外する.
 *
 * @param editingPaymentId 編集中の支払情報 ID
 * @param registeredPayerId 登録済みの支払元 ID
 */
fun List<PaymentEntity>.toPaymentSelectUiModel(
    editingPaymentId: Int,
): List<PaymentSelectUiState.Success.Item> {

    return map { it.toPaymentSelectUiModel(editingPaymentId) }
}