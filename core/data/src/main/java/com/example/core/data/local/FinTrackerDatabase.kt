package com.example.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.data.local.dao.AccountDao
import com.example.core.data.local.dao.BudgetDao
import com.example.core.data.local.dao.CategoryDao
import com.example.core.data.local.dao.TransactionDao
import com.example.core.data.local.entity.AccountEntity
import com.example.core.data.local.entity.BudgetEntity
import com.example.core.data.local.entity.CategoryEntity
import com.example.core.data.local.entity.SyncMetadataEntity
import com.example.core.data.local.entity.TransactionEntity
import com.example.core.data.sync.SyncMetadataDao


@Database(
    entities = [TransactionEntity::class, AccountEntity::class, CategoryEntity::class, BudgetEntity::class, SyncMetadataEntity::class],
    version = 2,
    exportSchema = true
)
abstract class FinTrackerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun syncMetadataDao(): SyncMetadataDao
}