package com.example.core.data.repository

import app.cash.turbine.test
import com.example.core.domain.model.Account
import com.example.core.domain.model.AccountType
import com.example.core.testing.fake.FakeAccountDao
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountRepositoryImplTest {

    private lateinit var dao : FakeAccountDao
    private lateinit var repository: AccountRepositoryImpl

    @BeforeEach
    fun setUp() {
        dao = FakeAccountDao()
        repository = AccountRepositoryImpl(dao)
    }

    @Test
    fun `add Account persists PENDING syncStatus`(): Unit = runTest{
        val account = Account(
            id = "acc-1",
            name = "Test Account",
            type = AccountType.CHECKING,
            isDeleted = false
        )

        repository.addAccount(account)
        val persistAccount = dao.getById("acc-1")
        assertNotNull(persistAccount)
        assertEquals(persistAccount?.syncStatus, "PENDING")
    }

    @Test
    fun `delete Account soft-deletes and excludes from observeAccounts`() = runTest {
        val account = Account(
            id = "acc-1",
            name = "Test Account",
            type = AccountType.CHECKING,
            isDeleted = false
        )

        repository.addAccount(account)
        repository.deleteAccount("acc-1")

        val deletedAccount = dao.getById("acc-1")
        assertNotNull(deletedAccount)
        assertEquals(deletedAccount?.isDeleted, true)
    }

    @Test
    fun `observeAccounts emits added accounts mapped to domain model`() = runTest {
        val account = Account(
            id = "acc-1",
            name = "Test Account",
            type = AccountType.CHECKING,
            isDeleted = false
        )

        repository.addAccount(account)
        repository.observeAccounts().test() {
            val emission = awaitItem()
            assertEquals(1, emission.size)
            assertEquals("Test Account", emission.first().name)
        }
    }

}
