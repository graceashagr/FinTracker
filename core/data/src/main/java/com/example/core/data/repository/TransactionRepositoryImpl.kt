package com.example.core.data.repository

import com.example.core.data.local.dao.TransactionDao
import com.example.core.data.local.entity.TransactionEntity
import com.example.core.data.local.entity.toDomain
import com.example.core.domain.model.Transaction
import com.example.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository{
    override fun observeTransactions(): Flow<List<Transaction>> {
        return transactionDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.upsert(TransactionEntity(
            id = transaction.id,
            accountId = transaction.accountId,
            categoryId = transaction.categoryId,
            note = transaction.note,
            amountCents = transaction.amountCents,
            occurredAt = transaction.occurredAt.toEpochMilliseconds(),
            updatedAt = Clock.System.now().toEpochMilliseconds(),
            isDeleted = false,
            syncStatus = "PENDING"
        ))
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.markDeleted(transactionId, Clock.System.now().toEpochMilliseconds())
    }

    override fun observePendngTransactionCount(): Flow<Int> {
        return transactionDao.observePendingCount()
    }
}
