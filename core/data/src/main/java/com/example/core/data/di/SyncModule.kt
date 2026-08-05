package com.example.core.data.di

import com.example.core.data.sync.AccountSyncEngine
import com.example.core.data.sync.TransactionSyncEngine
import com.example.core.domain.sync.SyncEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {

    @Binds
    @IntoSet
    abstract fun bindsTransactionSyncEngine(impl: TransactionSyncEngine): SyncEngine

    @Binds
    @IntoSet
    abstract fun bindsAccountSyncEngine(impl: AccountSyncEngine): SyncEngine
}