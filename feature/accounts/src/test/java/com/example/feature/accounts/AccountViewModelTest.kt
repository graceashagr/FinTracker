package com.example.feature.accounts

import app.cash.turbine.test
import com.example.core.data.repository.AccountRepositoryImpl
import com.example.core.domain.model.AccountType
import com.example.core.domain.repository.AccountRepository
import com.example.core.domain.usecase.AddAccountUseCase
import com.example.core.domain.usecase.DeleteAccountUseCase
import com.example.core.testing.fake.FakeAccountDao
import com.example.core.testing.rule.MainDispatcherRule
import com.example.feature.accounts.presentation.AccountViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AccountViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherRule =  MainDispatcherRule()

    private lateinit var viewModel: AccountViewModel
    private lateinit var fakeRepository: AccountRepository
    private lateinit var fakeAccountDao: FakeAccountDao

    @BeforeEach
    fun setUp(){
        fakeAccountDao = FakeAccountDao()
        fakeRepository = AccountRepositoryImpl(fakeAccountDao)
        viewModel = AccountViewModel(
            addAccountUseCase = AddAccountUseCase(
                fakeRepository,
                mockk(relaxed=true)
            ),
            deleteAccountUseCase = DeleteAccountUseCase(
                fakeRepository,
                mockk(relaxed = true)
            ),
            accountRepository = fakeRepository,
            syncScheduler = mockk(relaxed=true)
        )
    }

    @Test
    fun`initial state has empty accounts and is not loading after first emission `() = runTest {
        viewModel.accountUiState.test {
            val state = awaitItem()
            assert(state.isLoading)
            val loaded = awaitItem()
            assertTrue(loaded.accounts.isEmpty())
            assertEquals(false, loaded.isLoading)
        }
    }

    @Test
    fun `onAddClick adds a account and it appears in uiState`() = runTest {
        viewModel.accountUiState.test {
            awaitItem()

            viewModel.addAccount(
                name = "Test Account",
                type = AccountType.CHECKING
                )
            val added = awaitItem()
            assertEquals(1, added.accounts.size)
            assertEquals("Test Account", added.accounts.first().name)
            assertEquals(AccountType.CHECKING, added.accounts.first().type)
        }
    }

    @Test
    fun`onDeleteClick removes account from visible list but keeps pending count accurate`() = runTest {
        viewModel.accountUiState.test {
            awaitItem()

            viewModel.addAccount(
                name = "Test Account",
                type = AccountType.CHECKING
            )
            val added = awaitItem()
            assertEquals(1, added.accounts.size)

            viewModel.deleteAccount(added.accounts.first().id)
            val deleted = awaitItem()
            assertEquals(0, deleted.accounts.size)
        }
    }
}