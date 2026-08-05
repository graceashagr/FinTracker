package com.example.core.domain.usecase

import com.example.core.domain.repository.AccountRepository
import com.example.core.domain.sync.SyncScheduler
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(accountId: String) {
        accountRepository.deleteAccount(accountId)
        syncScheduler.triggerImmediateSync()
    }
}