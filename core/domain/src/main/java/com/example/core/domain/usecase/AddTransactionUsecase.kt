package com.example.core.domain.usecase

import com.example.core.domain.model.Transaction
import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.sync.SyncScheduler
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val syncScheduler: SyncScheduler,
){

    suspend operator fun invoke(accountId: String, categoryId: String, amountCents: Long, note: String){
      transactionRepository.addTransaction(
          Transaction(
              id = UUID.randomUUID().toString(),
              accountId = accountId,
              categoryId = categoryId,
              note = note,
              amountCents = amountCents,
              occurredAt = Clock.System.now(),
          )
      )
        syncScheduler.triggerImmediateSync()
    }
}
