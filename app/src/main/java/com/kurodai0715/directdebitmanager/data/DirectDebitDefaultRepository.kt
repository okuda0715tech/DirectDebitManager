package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.DestWithSource
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.data.source.TransferItem
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalSource
import com.kurodai0715.directdebitmanager.data.source.toExternal
import com.kurodai0715.directdebitmanager.data.source.toLocal
import com.kurodai0715.directdebitmanager.di.IoDispatcher
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
    suspend fun upsertDestination(id: Int, dest: String, sourceId: Int): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            val destination = Destination(id = id, name = dest, sourceId = sourceId)
            resultSuccess = try {
                localDataSource.upsertDestination(destination.toLocal())
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
            val destination = Destination(id = id, name = dest, sourceId = sourceId)
            numOfDeleted = try {
                localDataSource.deleteDestination(destination.toLocal())
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
    suspend fun upsertSource(id: Int, label: String, type: Int): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            val source = TransferItem(
                id = id,
                label = label,
                isSourceItem = true,
                type = type,
                sourceId = null
            )
            resultSuccess = try {
                localDataSource.upsertSource(source.toLocal())
                true
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                false
            }
            Log.d(TAG, "resultSuccess = $resultSuccess")
        }
        return resultSuccess
    }

    suspend fun fetchNumOfDestination(sourceId: Int): Int {
        var numOfDestination: Int
        withContext(ioDispatcher) {
            numOfDestination = try {
                localDataSource.fetchNumOfDestination(sourceId)
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
     * @param name 削除するレコードの source
     * @return [LocalDestination] テーブルと [LocalSource] テーブルの削除したレコードの件数。
     * エラーが発生した場合は -1。
     */
    suspend fun deleteDestAndSource(id: Int, name: String, type: Int): Pair<Int, Int> {
        var numOfDeleted: Pair<Int, Int>
        withContext(ioDispatcher) {
            val source = Source(id = id, name = name, type = type)
            numOfDeleted = try {
                localDataSource.deleteDestAndSourceBy(source.toLocal())
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                (-1 to -1)
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
            localTransSources.map { it.toExternal() }
        }
    }

    /**
     * 振替先と振替元を取得するストリーム.
     */
    fun fetchDestWithSourcesStream(): Flow<List<DestWithSource>> {
        return localDataSource.observeDestWithSource().map { localDestWithSource ->
            localDestWithSource.map { it.toExternal() }
        }
    }
}

