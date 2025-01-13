package com.kurodai0715.directdebitmanager.data

import android.util.Log
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
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

    suspend fun upsert(id: Int, dest: String, source: String): Boolean {
        var resultSuccess: Boolean
        withContext(ioDispatcher) {
            val directDebit = DirectDebit(id = id, destination = dest, source = source)
            resultSuccess = try {
                localDataSource.upsert(directDebit.toLocal())
                true
            } catch (e: Exception) {
                false
            }
            Log.d(TAG, "resultSuccess = $resultSuccess")
        }
        return resultSuccess
    }

    fun fetchDirectDebitStream(): Flow<List<DirectDebit>> {
        return localDataSource.observeDirectDebit().map { localDirectDebits ->

            val directDebits = mutableListOf<DirectDebit>()

            for (localDirectDebit in localDirectDebits) {
                directDebits.add(localDirectDebit.toExternal())
            }

            directDebits
        }
    }
}

