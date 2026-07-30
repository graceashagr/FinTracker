package com.example.core.data.sync

import com.example.core.data.local.entity.TransactionEntity
import javax.inject.Inject

class TransactionConflictResolver @Inject constructor() {

    fun merge(local: TransactionEntity?, incoming: TransactionEntity) : TransactionEntity {
        if(local == null) return incoming.copy(syncStatus = "SYNCED")
        if(local.isDeleted) return local
        if(incoming.isDeleted) return incoming.copy(syncStatus = "SYNCED")
        return if(incoming.updatedAt >= local.updatedAt) incoming.copy(syncStatus = "SYNCED") else local
    }
}