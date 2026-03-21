// TODO 著作権の宣言

package com.kurodai0715.directdebitmanager.domain.model

// TODO プライマリコンストラクタを private にして、 create 関数経由でのみインスタンスの生成を許可する。
data class Payee(
    val id: Int,
    val name: String,
) {
    companion object {
        fun create(
            id: Int,
            label: String,
        ): Payee {
            require(label.isNotBlank()) { "label is blank" }

            return Payee(id, label)
        }
    }
}
