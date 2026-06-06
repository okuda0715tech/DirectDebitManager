package com.kurodai0715.directdebitmanager.ui.screen.payment_select

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2
import com.kurodai0715.directdebitmanager.ui.common_ui.elements.ItemState

/**
 * エンティティを UI モデルに変換する(個別用).
 *
 * @param editingPaymentId 編集中の支払情報 ID
 * @param registeredPayerId 登録済みの支払元 ID
 */
fun PaymentEntityV2.toPaymentSelectUiModel(
    editingPaymentId: Int,
    registeredPayerId: Int
): PaymentSelectUiState.Success.Item {
    return PaymentSelectUiState.Success.Item(
        id = id,
        name = label,
        state = isRegistered(editingPaymentId, registeredPayerId)
    )
}

/**
 * 現在編集中の支払情報に対する「支払元」あるいは「支払先」に既に登録されているか、されていないかのステータスを返す.
 */
private fun PaymentEntityV2.isRegistered(editingPaymentId: Int, registeredPayerId: Int): ItemState {
    return if (editingPaymentId == parentId) { // 支払先として登録されている場合
        ItemState.Registered
    } else if (registeredPayerId == id) { // 支払元として登録されている場合
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
fun List<PaymentEntityV2>.toPaymentSelectUiModel(
    editingPaymentId: Int,
    registeredPayerId: Int
): List<PaymentSelectUiState.Success.Item> {

    return this
        // 自分自身はリストから除外する(支払先や支払元に自分自身が設定されるのはおかしいため)
        .filter { it.id != editingPaymentId }
        .map { it.toPaymentSelectUiModel(editingPaymentId, registeredPayerId) }
}