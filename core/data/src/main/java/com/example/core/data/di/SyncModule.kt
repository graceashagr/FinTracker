package com.example.core.data.di

import com.example.core.data.sync.TransactionSyncEngine
import com.example.core.domain.sync.SyncEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    abstract fun bindsSyncEngine(impl: TransactionSyncEngine): SyncEngine
}