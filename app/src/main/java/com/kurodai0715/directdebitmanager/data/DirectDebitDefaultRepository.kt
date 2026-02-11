/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.data.source.local.toTransferInfo
import com.kurodai0715.directdebitmanager.di.IoDispatcher
import com.kurodai0715.directdebitmanager.domain.model.TransferInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
     * 振替先情報を新規に登録する.
     */
    suspend fun createDestination(
        label: String,
        isSourceItem: Boolean,
        parentId: Int,
    ): Boolean {

        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertTransferItem(
                    TransferItemEntity(
                        label = label,
                        isSourceItem = isSourceItem,
                        typeCode = null,
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
     * 振替先情報を更新する.
     */
    suspend fun updateDestination(
        id: Int,
        label: String,
        isSourceItem: Boolean,
        parentId: Int,
    ): Boolean {

        val item = localDataSource.getItem(id)

        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            resultSuccess = try {
                localDataSource.upsertTransferItem(
                    item.copy(
                        id = id,
                        label = label,
                        isSourceItem = isSourceItem,
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
                    TransferItemEntity(
                        id = id,
                        label = name,
                        isSourceItem = true,
                        typeCode = type,
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
    fun loadSourcesStream(): Flow<List<TransferItemEntity>> {
        return localDataSource.observeByIsSource(isSource = true)
    }

    /**
     * 振替先と振替元の一覧を取得するストリーム.
     */
    fun loadTransferItemsStream(): Flow<List<TransferItemEntity>> {
        return localDataSource.observeTransferItems()
    }

    /**
     * 振替先の ID に基づき、振替先と振替元を紐づけて取得する.
     */
    suspend fun loadTransferInfo(destId: Int): TransferInfo {
        return localDataSource.getTransferInfo(destId).toTransferInfo()
    }

    /**
     * 指定した id のレコードを取得.
     */
    suspend fun loadItem(id: Int): TransferItemEntity {
        return localDataSource.getItem(id)
    }
}

