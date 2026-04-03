package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2

private const val TAG = "TransferRelationListMapper.kt"

fun PaymentEntityV2.toTransferRelationListItem(): TransferRelationListUiState.Success.Item {
    return TransferRelationListUiState.Success.Item(
        id = id,
        name = label,
        payerId = parentId,
    )
}

fun List<PaymentEntityV2>.toTransferRelationList(): List<TransferRelationListUiState.Success.Item> {
    return map { it.toTransferRelationListItem() }
}

/**
 * 要素の親子関係を解析し、ツリー構造に変換する.
 */
fun List<PaymentEntityV2>.buildNestedTree(): NestedTreeItem {

    /**
     * レシーバーオブジェクトを親として、ツリー型になるように、その子を追加する.
     * <p>
     * 再帰的に呼び出して、親から子へ、子から孫へと処理を繰り返す。
     * 深さ優先探索 ( Depth-First Search ) で、再起呼び出しする。
     *
     * @param list 変換前のリスト
     */
    fun NestedTreeItem.buildSubTree(list: List<PaymentEntityV2>) {
        list.forEach {
            if (this.id == it.parentId) {
                val child = it.toNestedTreeItem()
                this.childList.add(child)
                child.buildSubTree(list)
            }
        }
    }

    val root = NestedTreeItem(
        id = 0,
        label = "root",
        parentId = 0,
        childList = mutableListOf()
    )

    root.buildSubTree(this)

    Log.d(TAG, "root: $root")

    return root
}

data class NestedTreeItem(
    val id: Int,
    val label: String,
    val parentId: Int,
    val childList: MutableList<NestedTreeItem> = mutableListOf(),
)

fun PaymentEntityV2.toNestedTreeItem(): NestedTreeItem {
    return NestedTreeItem(
        id = id,
        label = label,
        parentId = parentId ?: 0,
    )
}

/**
 * ネスト型のツリーを深さ情報を持ったフラット型のリストへ展開する.
 *
 * @param nestedTreeRoot 変換前のツリーのルート
 * @return 変換後のフラットツリー
 */
fun NestedTreeItem.flattenTree(): List<FlattenedTreeItem> {

    val result = mutableListOf<FlattenedTreeItem>()

    /**
     * 引数で渡されたノードをフラット型のツリーに格納する.
     * <p>
     * 深さ優先探索 ( Depth-First Search ) で、再起呼び出しする。
     *
     * @param depth このアイテムの深さ ( 0 がルート)
     */
    fun NestedTreeItem.flattenChild(depth: Int) {
        val repeat = depth - 1
        val indexedLabel =
            if (repeat >= 0)
                "     ".repeat(repeat) + "└ " + label
            else
                label
        result.add(FlattenedTreeItem(id, indexedLabel, depth))
        childList.forEach { it.flattenChild(depth + 1) }
    }

    flattenChild(0)

    Log.d(TAG, "result: $result")

    return result
}

data class FlattenedTreeItem(
    val id: Int,
    val label: String,
    val depth: Int,
)