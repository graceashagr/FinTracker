package com.example.core.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.core.data.local.FinTrackerDatabase
import com.example.core.data.local.dao.AccountDao
import com.example.core.data.local.dao.BudgetDao
import com.example.core.data.local.dao.CategoryDao
import com.example.core.data.local.dao.TransactionDao
import com.example.core.data.sync.SyncMetadataDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFinTrackerDatabase(@ApplicationContext context: Context) : FinTrackerDatabase {
        return Room.databaseBuilder(
            context,
            FinTrackerDatabase::class.java,
            "fin_tracker_database.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideTransactionDao(db : FinTrackerDatabase) : TransactionDao = db.transactionDao()

    @Provides
    fun provideAccountDao(db: FinTrackerDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideBudgetDao(db: FinTrackerDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun providesCategoryDao(db: FinTrackerDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun providesSyncMetadataDao(db: FinTrackerDatabase): SyncMetadataDao = db.syncMetadataDao()
}