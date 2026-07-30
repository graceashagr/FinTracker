package com.example.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE isDeleted = 0")
    fun observeAll(): Flow<List<AccountEntity>>

    @Upsert
    suspend fun upsert(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE type=:type AND isDeleted = 0")
    fun observeByType(type : String): Flow<List<AccountEntity>>

    @Query("UPDATE accounts SET isDeleted = 1 ,updatedAt = :now, syncStatus = 'PENDING' WHERE id = :id")
    suspend fun markDeleted(id: String, now: Long)

    @Query("SELECT * FROM accounts WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSync(): List<AccountEntity>

    @Query("UPDATE accounts SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: String)

}