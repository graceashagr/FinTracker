package com.example.core.domain.usecase

import com.example.core.domain.model.Transaction
import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.sync.SyncScheduler
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(transaction: Transaction){
        transactionRepository.addTransaction(transaction)
//        syncScheduler.triggerImmediateSync()
    }
}