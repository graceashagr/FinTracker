package com.example.core.data.di

import com.example.core.data.repository.AccountRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.example.core.data.repository.TransactionRepositoryImpl
import com.example.core.data.sync.WorkManagerSyncScheduler
import com.example.core.domain.repository.AccountRepository
import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.sync.SyncScheduler

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    @Binds
    abstract fun bindSyncScheduler(
        syncSchedulerImpl: WorkManagerSyncScheduler
    ): SyncScheduler

    @Binds
    abstract fun bindAccountRepository(
        accountRespositoryImpl: AccountRepositoryImpl
    ): AccountRepository
}