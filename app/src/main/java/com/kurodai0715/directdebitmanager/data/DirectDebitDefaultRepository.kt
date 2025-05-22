package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.data.source.local.DirectDebitDao
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
     * 口座振替情報を DB へ登録する.
     */
    suspend fun upsertDestination(id: Int, dest: String, sourceId: Int, source: String): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            val destination = Destination(id = id, name = dest, sourceId = sourceId, sourceName = source)
            resultSuccess = try {
                localDataSource.upsert(destination.toLocal())
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
     * 口座振替情報を DB から削除する.
     *
     * @param id 削除するレコードの id
     * @param dest 削除するレコードの destination
     * @param source 削除するレコードの source
     * @return 削除したレコードの件数。エラーが発生した場合は -1。
     */
    suspend fun deleteDestination(id: Int, dest: String, sourceId: Int, source: String): Int {
        var numOfDeleted: Int
        withContext(ioDispatcher) {
            val destination = Destination(id = id, name = dest, sourceId = sourceId, sourceName = source)
            numOfDeleted = try {
                localDataSource.delete(destination.toLocal())
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                -1
            }
            Log.d(TAG, "NumOfDeleted = $numOfDeleted")
        }
        return numOfDeleted
    }

    /**
     * 口座振替情報を取得するストリーム.
     */
    fun fetchDestinationsStream(): Flow<List<Destination>> {
        return localDataSource.observeDirectDebit().map { localDirectDebits ->
            localDirectDebits.map { it.toExternal() }
        }
    }

    /**
     * 振替元情報を DB へ登録する.
     */
    suspend fun upsertSource(id: Int, source: String): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            val source = Source(id = id, name = source)
            resultSuccess = try {
                localDataSource.upsert(source.toLocal())
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
     * 振替元情報を DB から削除する.
     *
     * @param id 削除するレコードの id
     * @param source 削除するレコードの source
     * @return 削除したレコードの件数。エラーが発生した場合は -1。
     */
    suspend fun deleteSource(id: Int, source: String): Int {
        var numOfDeleted: Int
        withContext(ioDispatcher) {
            val source = Source(id = id, name = source)
            numOfDeleted = try {
                localDataSource.delete(source.toLocal())
            } catch (e: Exception) {
                Log.e(TAG, "$e")
                -1
            }
            Log.d(TAG, "NumOfDeleted = $numOfDeleted")
        }
        return numOfDeleted
    }

    /**
     * 振替元情報を取得するストリーム.
     */
    fun fetchSourcesStream(): Flow<List<Source>> {
        return localDataSource.observeTransSource().map { localTransSources ->
            localTransSources.map { it.toExternal() }
        }
    }

}

