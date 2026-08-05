package com.example.core.data.repository

import com.example.core.data.local.dao.AccountDao
import com.example.core.data.local.entity.AccountEntity
import com.example.core.data.local.entity.toDomain
import com.example.core.domain.model.Account
import com.example.core.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
): AccountRepository {
    override fun observeAccounts(): Flow<List<Account>> {
        return accountDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addAccount(account: Account) {
        accountDao.upsert(
            AccountEntity(
                id = account.id,
                name = account.name,
                type = account.type.name,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
                isDeleted = false,
                syncStatus = "PENDING"
            )
        )
    }

    override suspend fun deleteAccount(accountId: String) {
        accountDao.markDeleted(accountId, Clock.System.now().toEpochMilliseconds())
    }

}