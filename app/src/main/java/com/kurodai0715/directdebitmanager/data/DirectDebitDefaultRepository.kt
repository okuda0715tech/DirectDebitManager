package com.kurodai0715.directdebitmanager.data

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

@Singleton
class DirectDebitDefaultRepository @Inject constructor(
    private val localDataSource: DirectDebitDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun insert(dest: String, source: String) {
        withContext(ioDispatcher) {
            val directDebit = DirectDebit(destination = dest, source = source)
            localDataSource.insert(directDebit.toLocal())
        }
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

