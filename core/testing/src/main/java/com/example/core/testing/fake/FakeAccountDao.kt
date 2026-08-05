package com.example.core.testing.fake

import com.example.core.data.local.dao.AccountDao
import com.example.core.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeAccountDao: AccountDao {
    private val accounts = MutableStateFlow<Map<String, AccountEntity>>(emptyMap())
    override fun observeAll(): Flow<List<AccountEntity>> {
        return accounts.map { accounts ->
            accounts.values.filter {
                !it.isDeleted
            }
        }
    }

    override suspend fun upsert(account: AccountEntity) {
        accounts.update { it + (account.id to account) }
    }

    override fun observeByType(type: String): Flow<List<AccountEntity>> {
        return accounts.map { accounts ->
            accounts.values.filter { it.type == type && !it.isDeleted }
        }
    }

    override suspend fun markDeleted(id: String, now: Long) {
        accounts.update {
            val account = it[id]
            if (account != null) {
                it + (id to account.copy(isDeleted = true, updatedAt = now))
            } else {
                it
            }
        }
    }

    override suspend fun getPendingSync(): List<AccountEntity> {
        return accounts.value.values.filter { it.syncStatus == "PENDING" }
    }

    override suspend fun markSynced(id: String) {
        accounts.update {
            val account = it[id]
            if (account != null) {
                it + (id to account.copy(syncStatus = "SYNCED"))
            } else {
                it
            }
        }
    }

    override suspend fun getById(id: String): AccountEntity? {
        return accounts.value[id]
    }
}