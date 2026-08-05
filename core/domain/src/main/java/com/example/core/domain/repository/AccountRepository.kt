package com.example.core.domain.repository

import com.example.core.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun observeAccounts() : Flow<List<Account>>
    suspend fun addAccount(account: Account)
    suspend fun deleteAccount(accountId: String)
}