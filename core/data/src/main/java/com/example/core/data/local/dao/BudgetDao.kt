package com.example.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Query("SELECT * FROM budgets WHERE isDeleted = 0")
    fun observeAll(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE month = :month AND isDeleted = 0")
    fun observeByMonth(month: String): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND isDeleted = 0")
    fun observeByCategory(categoryId: String) : Flow<List<BudgetEntity>>

    @Upsert
    suspend fun upsertBudget(budget: BudgetEntity)

    @Query("UPDATE budgets SET isDeleted = 1, updatedAt = :now, syncStatus = 'PENDING' WHERE id = :id")
    suspend fun markDeleted(id: String, now: Long)

    @Query("SELECT * FROM budgets WHERE syncStatus = 'PENDING'")
    suspend fun getPendingSync() : List<BudgetEntity>

    @Query("UPDATE budgets SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markSynced(id: String)
}