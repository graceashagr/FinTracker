package com.example.core.testing.fake

import com.example.core.data.local.dao.TransactionDao
import com.example.core.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeTransactionDao : TransactionDao {

    private val transactions = MutableStateFlow<Map<String, TransactionEntity>>(emptyMap())
    override fun observeAll(): Flow<List<TransactionEntity>> {
       return transactions.map { transactions ->
            transactions.values.filter { !it.isDeleted }.sortedByDescending { it.occurredAt }
        }
    }

    override fun observeByAccount(accountId: String): Flow<List<TransactionEntity>> {
        return transactions.map { transactions ->
            transactions.values.filter { it.accountId == accountId && !it.isDeleted }.sortedByDescending { it.occurredAt}
        }
    }

    override suspend fun getById(id: String): TransactionEntity? {
        return transactions.value[id]
    }

    override suspend fun upsert(transaction: TransactionEntity) {
        transactions.update {
            it + (transaction.id to transaction)
        }
    }

    override suspend fun markDeleted(id: String, now: Long) {
        transactions.update {
            val transaction = it[id]
            if (transaction != null) {
                it + (id to transaction.copy(isDeleted = true, updatedAt = now, syncStatus = "PENDING"))
            } else {
                it
            }
        }
    }

    override suspend fun getPendingSync(): List<TransactionEntity> =
        transactions.value.values.filter { it.syncStatus == "PENDING"}

    override suspend fun markSynced(id: String) {
        transactions.update {
            val transaction = it[id]
            if (transaction != null) {
                it + (id to transaction.copy(syncStatus = "SYNCED"))
            } else {
                it
            }
        }
    }

    override fun observePendingCount(): Flow<Int> =
        transactions.map { it.values.count { txn -> txn.syncStatus == "PENDING" } }
}