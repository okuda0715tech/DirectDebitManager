package com.kurodai0715.directdebitmanager.ui.screen.destination_list

/**
 * 要素の親子関係を解析し、ツリー構造に変換する.
 *
 * @param list 変換前のリスト
 */
fun buildNestedTree(list: List<DestWithSourceUiModel>): NestedTreeItemUiModel {

    /**
     * レシーバーオブジェクトを親として、ツリー型になるように、その子を追加する.
     * <p>
     * 再帰的に呼び出して、親から子へ、子から孫へと処理を繰り返す。
     * 深さ優先探索 ( Depth-First Search ) で、再起呼び出しする。
     *
     * @param list 変換前のリスト
     */
    fun NestedTreeItemUiModel.addChildren(list: List<DestWithSourceUiModel>) {
        list.forEach {
            if (this.destId == it.sourceId) {
                val treeItem = it.toNestedTreeItemUiModel()
                this.children.add(treeItem)
                treeItem.addChildren(list)
            }
        }
    }

    val root = NestedTreeItemUiModel(
        destId = 0,
        sourceId = 0,
        label = "root",
        children = mutableListOf()
    )

    root.addChildren(list)

    return root
}

/**
 * ネスト型のツリーを深さ情報を持ったフラット型のリストへ展開する.
 *
 * @param nestedTreeRoot 変換前のツリーのルート
 * @return 変換後のフラットツリー
 */
fun flattenTree(nestedTreeRoot: NestedTreeItemUiModel): List<FlattenedTreeItemUiModel> {

    val result = mutableListOf<FlattenedTreeItemUiModel>()

    /**
     * 引数で渡されたノードをフラット型のツリーに格納する.
     * <p>
     * 深さ優先探索 ( Depth-First Search ) で、再起呼び出しする。
     *
     * @param nestedTreeNode 変換前のツリーのノード
     * @param depth [nestedTreeNode] の深さ ( 0 がルート)
     */
    fun flatten(nestedTreeNode: NestedTreeItemUiModel, depth: Int) {
        result.add(FlattenedTreeItemUiModel(nestedTreeNode.destId, nestedTreeNode.label, depth))
        nestedTreeNode.children.forEach { flatten(it, depth + 1) }
    }

    flatten(nestedTreeRoot, 0)

    return result
}

/**
 * モデルの変換
 */
fun DestWithSourceUiModel.toNestedTreeItemUiModel() = NestedTreeItemUiModel(
    destId = destId,
    sourceId = sourceId,
    label = destName,
)