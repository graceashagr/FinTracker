package com.example.core.data.sync

import com.example.core.data.local.entity.AccountEntity
import javax.inject.Inject

class AccountConflictResolver @Inject constructor() {
    fun merge(local: AccountEntity?,incoming: AccountEntity) : AccountEntity {
        if (local == null) return incoming.copy(syncStatus = "SYNCED")
        if (local.isDeleted) return local
        if (incoming.isDeleted) return incoming.copy(syncStatus = "SYNCED")
        return if(incoming.updatedAt >= local.updatedAt) incoming.copy(syncStatus = "SYNCED") else local
    }
}