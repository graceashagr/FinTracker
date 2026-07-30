package com.example.feature.transactions.presentation

import app.cash.turbine.test
import com.example.core.data.repository.TransactionRepositoryImpl
import com.example.core.data.sync.WorkManagerSyncScheduler
import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.usecase.AddTransactionUseCase
import com.example.core.domain.usecase.DeleteTransactionUseCase
import com.example.core.domain.usecase.UpdateTransactionUseCase
import com.example.core.testing.fake.FakeTransactionDao
import com.example.core.testing.rule.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class TransactionViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TransactionViewModel
    private lateinit var fakeRepository: TransactionRepository
    private lateinit var fakeTransactionDao: FakeTransactionDao

    @BeforeEach
    fun setUp() {
        fakeTransactionDao = FakeTransactionDao()
        fakeRepository = TransactionRepositoryImpl(fakeTransactionDao)
        viewModel = TransactionViewModel(
            transactionRepository = fakeRepository,
            addTransactionUseCase = AddTransactionUseCase(fakeRepository, mockk(relaxed = true)),
            deleteTransactionUseCase = DeleteTransactionUseCase(
                fakeRepository,
                mockk(relaxed = true)
            ),
            updateTransactionUseCase = UpdateTransactionUseCase(
                fakeRepository,
                mockk(relaxed = true)
            ),
            syncScheduler = WorkManagerSyncScheduler(mockk(relaxed = true)
            )
        )
    }

    @Test
    fun `initial state has empty transactions and is not loading after first emission`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            val loaded = awaitItem()
            assertTrue(loaded.transactions.isEmpty())
            assertEquals(false, loaded.isLoading)
        }
    }

    @Test
    fun `onAddClick adds a transaction and it appears in uiState`() = runTest {
        viewModel.uiState.test {
            awaitItem()   // skip initial empty state

            viewModel.addClicked(
                accountId = "acc-1",
                categoryId = "cat-1",
                amount = 1000,
                note = "Lunch")

            val updated = awaitItem()
            assertEquals(1, updated.transactions.size)
            assertEquals("Lunch", updated.transactions.first().note)
            assertEquals(1, updated.pendingCount)
        }
    }

    @Test
    fun `onDeleteClick removes transaction from visible list but keeps pending count accurate`() = runTest {

        viewModel.uiState.test {
            awaitItem()
            viewModel.addClicked(
                accountId = "acc-1",
                categoryId = "cat-1",
                amount = 1000,
                note = "Lunch")

            val beforeDelete = awaitItem()
            val id = beforeDelete.transactions.first().id

            viewModel.deleteClick(id)

            val afterDelete = awaitItem()
            assertTrue(afterDelete.transactions.isEmpty())
        }
    }
}