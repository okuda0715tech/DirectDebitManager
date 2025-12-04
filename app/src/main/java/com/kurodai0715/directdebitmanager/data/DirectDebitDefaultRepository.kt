package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.DestinationUiModel
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.data.source.TransferItem
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.data.source.toExternal
import com.kurodai0715.directdebitmanager.data.source.toLocalTransferItem
import com.kurodai0715.directdebitmanager.data.source.toSource
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "DirectDebitDefaultRepository.kt"

@Singleton
class DirectDebitDefaultRepository @Inject constructor(
    private val localDataSource: DirectDebitDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    /**
     * 振替先情報を DB へ登録する.
     */
    suspend fun upsertDestination(
        id: Int,
        label: String,
        isSourceItem: Boolean,
        type: TransferItemType?,
        parentId: Int,
    ): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertTransferItem(
                    LocalTransferItem(
                        id = id,
                        label = label,
                        isSourceItem = isSourceItem,
                        type = type?.let { TransferItemType.toInt(type) },
                        parentId = parentId
                    )
                )
                true
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                false
            }
            Log.d(TAG, "resultSuccess = $resultSuccess")
        }
        return resultSuccess
    }

    /**
     * 振替先情報を DB から削除する.
     *
     * @param id 削除するレコードの id
     * @param dest 削除するレコードの destination
     * @param sourceId 削除するレコードの sourceId
     * @return 削除したレコードの件数。エラーが発生した場合は -1。
     */
    suspend fun deleteDestination(id: Int, dest: String, sourceId: Int): Int {
        var numOfDeleted: Int
        withContext(ioDispatcher) {
            val destinationUiModel = DestinationUiModel(id = id, name = dest, sourceId = sourceId)
            numOfDeleted = try {
                localDataSource.deleteDestination(destinationUiModel.toLocalTransferItem())
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                -1
            }
            Log.d(TAG, "NumOfDeleted = $numOfDeleted")
        }
        return numOfDeleted
    }

    /**
     * 振替元情報を DB へ登録する.
     */
    suspend fun upsertSource(id: Int, name: String, type: Int): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertTransferItem(
                    LocalTransferItem(
                        id = id,
                        label = name,
                        isSourceItem = true,
                        type = type,
                        parentId = null,
                    )
                )
                true
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                false
            }
            Log.d(TAG, "resultSuccess = $resultSuccess")
        }
        return resultSuccess
    }

    /**
     * 引数で指定されたデータを sourceId として参照しているレコードの件数を取得する.
     *
     * @param id sourceId
     * @return id を sourceId として参照しているレコードの件数。エラーが発生した場合は -1。
     */
    suspend fun countDestinationsReferencing(id: Int): Int {
        var numOfDestination: Int
        withContext(ioDispatcher) {
            numOfDestination = try {
                localDataSource.countDestinationsReferencing(id)
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                -1
            }
            Log.d(TAG, "numOfDestination = $numOfDestination")
        }
        return numOfDestination
    }

    /**
     * 振替元情報を DB から削除する.
     *
     * @param id 削除するレコードの id
     * @param name 削除するレコードの name
     * @return 削除したレコードの件数。エラーが発生した場合は -1。
     */
    suspend fun deleteSource(id: Int): Int {
        var numOfDeleted: Int
        withContext(ioDispatcher) {
            numOfDeleted = try {
                localDataSource.deleteSource(LocalTransferItem(id, "", true, null, null))
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                -1
            }
            Log.d(TAG, "numOfDeleted = $numOfDeleted")
        }
        return numOfDeleted
    }

    /**
     * 振替元情報を取得するストリーム.
     */
    fun fetchSourcesStream(): Flow<List<Source>> {
        return localDataSource.observeSources().map { localTransSources ->
            localTransSources.map { it.toSource() }
        }
    }

    /**
     * 振替先と振替元を取得するストリーム.
     */
    fun fetchTransferItemsStream(): Flow<List<TransferItem>> {
        return localDataSource.observeTransferItems().map { localTransferItem ->
            localTransferItem.map { it.toExternal() }
        }
    }
}

