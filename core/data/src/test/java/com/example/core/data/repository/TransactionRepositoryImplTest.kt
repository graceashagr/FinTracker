package com.example.core.data.repository

import app.cash.turbine.test
import com.example.core.domain.model.Transaction
import com.example.core.testing.fake.FakeTransactionDao
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class TransactionRepositoryImplTest {

    private lateinit var fakeDao: FakeTransactionDao
    private lateinit var repository: TransactionRepositoryImpl

    @BeforeEach
    fun setUp() {
        fakeDao = FakeTransactionDao()
        repository = TransactionRepositoryImpl(fakeDao)
    }

    @Test
    fun `addTransaction persists PENDING syncStatus`(): Unit = runTest {
        val transaction = Transaction(
            id = "txn-1",
            accountId = "acc-1",
            categoryId = "cat-1",
            note = "Test transaction",
            amountCents = 1000,
            occurredAt = Clock.System.now()
        )

        repository.addTransaction(transaction)

        val persistedTransaction = fakeDao.getById("txn-1")
        assertNotNull(persistedTransaction)
        assertEquals("PENDING", persistedTransaction?.syncStatus)

    }
    @Test
    fun `observeTransactions emits added transactions mapped to domain model`() = runTest {
        val transaction = Transaction(
            id = "txn-1", accountId = "acc-1", categoryId = "cat-1",
            amountCents = 1500, note = "Coffee", occurredAt = Clock.System.now()
        )
        repository.addTransaction(transaction)

        repository.observeTransactions().test {
            val emission = awaitItem()
            assertEquals(1, emission.size)
            assertEquals("Coffee", emission.first().note)
        }
    }

    @Test
    fun `deleteTransaction soft-deletes and excludes from observeTransactions`() = runTest {
        val transaction = Transaction(
            id = "txn-1", accountId = "acc-1", categoryId = "cat-1",
            amountCents = 1500, note = "Coffee", occurredAt = Clock.System.now()
        )
        repository.addTransaction(transaction)

        repository.deleteTransaction("txn-1")

        repository.observeTransactions().test {
            val emission = awaitItem()
            assertTrue(emission.isEmpty())   // soft-deleted, filtered out of observeAll
        }

        val stillInDb = fakeDao.getById("txn-1")
        assertNotNull(stillInDb)               // row still exists...
        assertTrue(stillInDb!!.isDeleted)      // ...just marked deleted, not hard-removed
    }

    @Test
    fun `observePendingCount reflects transactions awaiting sync`() = runTest {
        val transaction = Transaction(
            id = "txn-1", accountId = "acc-1", categoryId = "cat-1",
            amountCents = 1500, note = "Coffee", occurredAt = Clock.System.now()
        )
        repository.addTransaction(transaction)

        fakeDao.observePendingCount().test {
            assertEquals(1, awaitItem())
        }

        fakeDao.markSynced("txn-1")

        fakeDao.observePendingCount().test {
            assertEquals(0, awaitItem())
        }
    }
}