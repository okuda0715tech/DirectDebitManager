/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.TransferItem
import com.kurodai0715.directdebitmanager.data.source.local.DestWithSourceLocal
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.data.source.toExternal
import com.kurodai0715.directdebitmanager.data.source.toSource
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import com.kurodai0715.directdebitmanager.domain.model.Source
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
        id: Int?,
        label: String,
        isSourceItem: Boolean,
        type: TransferItemType?,
        parentId: Int,
    ): Boolean {
        requireNotNull(id) { "id is null" }

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
     * 振替元情報を DB へ登録する.
     */
    suspend fun upsertSource(id: Int, name: String, type: Int, parentId: Int): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertTransferItem(
                    LocalTransferItem(
                        id = id,
                        label = name,
                        isSourceItem = true,
                        type = type,
                        parentId = parentId,
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
    suspend fun countDestinationsReferencing(id: Int?): Int {
        requireNotNull(id) { "id is null" }

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
     * 振替情報を DB から削除する.
     *
     * @param id 削除するレコードの id
     * @return 削除したレコードの件数。エラーが発生した場合は -1。
     */
    suspend fun deleteItemBy(id: Int?): Int {
        requireNotNull(id) { "id is null" }

        var numOfDeleted: Int
        withContext(ioDispatcher) {
            numOfDeleted = try {
                localDataSource.deleteItem(id)
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
    fun loadSourcesStream(): Flow<List<Source>> {
        return localDataSource.observeSources().map { localTransSources ->
            localTransSources.map { it.toSource() }
        }
    }

    /**
     * 振替元情報を取得するストリーム.
     */
    fun loadSourcesStream2(): Flow<List<LocalTransferItem>> {
        return localDataSource.observeSources()
    }

    /**
     * 振替先と振替元の一覧を取得するストリーム.
     */
    fun loadTransferItemsStream(): Flow<List<TransferItem>> {
        return localDataSource.observeTransferItems().map { localTransferItem ->
            localTransferItem.map { it.toExternal() }
        }
    }

    /**
     * 振替元の ID に基づき、その明細を取得する.
     */
    suspend fun loadTransferItem(id: Int): DestWithSourceLocal {
        return localDataSource.getTransferItem(id)
    }

    /**
     * 指定した id のレコードを取得.
     */
    suspend fun loadItem(id: Int): LocalTransferItem {
        return localDataSource.getItem(id)
    }
}

