package com.example.core.domain.usecase

import com.example.core.domain.model.Account
import com.example.core.domain.model.AccountType
import com.example.core.domain.repository.AccountRepository
import com.example.core.domain.sync.SyncScheduler
import javax.inject.Inject

class AddAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val syncScheduler: SyncScheduler
) {
    suspend operator fun invoke(name: String, type: AccountType){
        accountRepository.addAccount(
            Account(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                type = type
            )
        )
        syncScheduler.triggerImmediateSync()
    }
}