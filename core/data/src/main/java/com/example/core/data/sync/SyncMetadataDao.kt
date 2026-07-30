package com.example.core.data.sync

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.data.local.entity.SyncMetadataEntity

@Dao
interface SyncMetadataDao {
    @Query("SELECT lastSyncedAt FROM sync_metadata WHERE tableName = :table")
    suspend fun getLastSyncedAt(table: String): Long?

    @Upsert
    suspend fun setLastSyncedAt(meta: SyncMetadataEntity)
}