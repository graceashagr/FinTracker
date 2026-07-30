package com.example.core.domain.usecase

import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.sync.SyncScheduler
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(transactionId: String) {
        transactionRepository.deleteTransaction(transactionId)
        syncScheduler.triggerImmediateSync()
    }
}