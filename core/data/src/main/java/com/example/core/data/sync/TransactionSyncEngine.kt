package com.example.core.data.sync

import android.util.Log
import com.example.core.data.sync.SyncMetadataDao
import com.example.core.data.local.dao.TransactionDao
import com.example.core.data.local.entity.SyncMetadataEntity
import com.example.core.data.local.entity.TransactionEntity
import com.example.core.data.remote.source.TransactionRemoteDataSource
import com.example.core.domain.sync.SyncEngine
import com.example.core.domain.sync.SyncResult
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class TransactionSyncEngine @Inject constructor(
    private val dao : TransactionDao,
    private val transactionRemoteDataSource: TransactionRemoteDataSource,
    private val syncMetaDao: SyncMetadataDao,
    private val conflictResolver: TransactionConflictResolver
): SyncEngine
{
    override suspend fun syncTransactions(): SyncResult = runCatching {
        pullRemoteChanges()
        pushLocalChanges()
    }.fold(
        onSuccess = { SyncResult.Success },
        onFailure = {
            Log.e("SyncEngine", "Sync failed", it)
            SyncResult.Failure(it)
        }
    )

    private suspend fun pushLocalChanges() {
        val pending = dao.getPendingSync()
        Log.d("SyncEngine", "Found ${pending.size} pending transactions")
        pending.forEach { local ->
            Log.d("SyncEngine", "Pushing ${local.id}")
            transactionRemoteDataSource.push(local)
            dao.markSynced(local.id)
            Log.d("SyncEngine", "Marked synced: ${local.id}")
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun pullRemoteChanges() {
        val lastSync = syncMetaDao.getLastSyncedAt("transactions") ?: 0L
        val remoteChanges = transactionRemoteDataSource.pullChangedSince(lastSync)

        remoteChanges.forEach { incoming ->
            val local = dao.getById(incoming.id)
            dao.upsert(conflictResolver.merge(local,incoming))
        }
        syncMetaDao.setLastSyncedAt(SyncMetadataEntity(
            tableName = "transactions",
            lastSyncedAt = Clock.System.now().toEpochMilliseconds()
        ))
    }
}