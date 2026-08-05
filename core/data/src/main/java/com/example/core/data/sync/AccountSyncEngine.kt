package com.example.core.data.sync

import android.util.Log
import com.example.core.data.local.dao.AccountDao
import com.example.core.data.local.entity.AccountEntity
import com.example.core.data.local.entity.SyncMetadataEntity
import com.example.core.data.remote.source.FireStoreRemoteDataSource
import com.example.core.domain.sync.SyncEngine
import com.example.core.domain.sync.SyncResult
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AccountSyncEngine @Inject constructor(
    private val accountDao: AccountDao,
    private val remoteDataSource: FireStoreRemoteDataSource<AccountEntity>,
    private val syncMetadataDao: SyncMetadataDao,
    private val conflictResolver: AccountConflictResolver
): SyncEngine {
    override val entityName: String
        get() = "Accounts"

    override suspend fun sync(): SyncResult = runCatching{
        pullRemoteChanges()
        pushLocalChanges()
    }.fold(
        onSuccess = { SyncResult.Success },
        onFailure = {
            Log.e("AccountSyncEngine", "Sync failed", it)
            SyncResult.Failure(it)
        }
    )


    private suspend fun pushLocalChanges() {
        val pendingSync = accountDao.getPendingSync()
        pendingSync.forEach { local ->
            remoteDataSource.push(local)
            accountDao.markSynced(local.id)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun pullRemoteChanges() {
        val lastSync = syncMetadataDao.getLastSyncedAt("accounts") ?: 0L
        val remoteChanges = remoteDataSource.pullChangedSince(lastSync)
        remoteChanges.forEach { incoming ->
            val local = accountDao.getById(incoming.id)
            accountDao.upsert(conflictResolver.merge(local, incoming))
        }
        syncMetadataDao.setLastSyncedAt(
            SyncMetadataEntity(
                tableName = "accounts",
                lastSyncedAt = Clock.System.now().toEpochMilliseconds()
            )
        )
    }
}