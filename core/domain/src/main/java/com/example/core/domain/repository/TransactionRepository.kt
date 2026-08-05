package com.example.core.domain.repository

import com.example.core.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeTransactions(): Flow<List<Transaction>>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transactionId: String)
    fun observePendingTransactionCount(): Flow<Int>
}