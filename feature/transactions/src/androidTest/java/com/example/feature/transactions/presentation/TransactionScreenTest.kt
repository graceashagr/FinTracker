package com.example.feature.transactions.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.core.data.repository.TransactionRepositoryImpl
import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.usecase.AddTransactionUseCase
import com.example.core.domain.usecase.DeleteTransactionUseCase
import com.example.core.domain.usecase.UpdateTransactionUseCase
import com.example.core.testing.fake.FakeTransactionDao
import com.example.core.ui.theme.FinTrackerTheme
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransactionScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var fakeDao: FakeTransactionDao
    private lateinit var repository: TransactionRepository
    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setUp(){
        fakeDao = FakeTransactionDao()
        repository = TransactionRepositoryImpl(fakeDao)
        viewModel = TransactionViewModel(
            addTransactionUseCase = AddTransactionUseCase(
                transactionRepository = repository,
                syncScheduler = mockk(relaxed = true)
            ),
            deleteTransactionUseCase = DeleteTransactionUseCase(
                repository,
                syncScheduler = mockk(relaxed = true)
            ),
            updateTransactionUseCase = UpdateTransactionUseCase(
                repository,
                syncScheduler = mockk(relaxed = true)
            ),
            transactionRepository = TransactionRepositoryImpl(
                transactionDao = fakeDao
            ),
            syncScheduler = mockk(relaxed = true)
        )
    }

    @Test
    fun emptyState_showsNoTransactions(){
        composeTestRule.setContent {
            FinTrackerTheme {
                TransactionScreen(
                    viewModel = viewModel,
                    showDebugControls = false
                )
            }
        }

        composeTestRule.onNodeWithText("Coffee").assertDoesNotExist()
    }

    @Test
    fun tapAddTransactionButton_addsTransaction(){
        composeTestRule.setContent {
            FinTrackerTheme {
                TransactionScreen(
                    viewModel = viewModel,
                    showDebugControls = false
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add Transaction").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Coffee").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Coffee").assertIsDisplayed()
    }

    @Test
    fun debugControls_hiddenWhenFlagIsFalse() {
        composeTestRule.setContent {
            FinTrackerTheme {
                TransactionScreen(viewModel = viewModel, showDebugControls = false)
            }
        }

        composeTestRule.onNodeWithText("Force sync").assertDoesNotExist()
    }

    @Test
    fun debugControls_showWhenFlagIsTrue(){
        composeTestRule.setContent {
            FinTrackerTheme {
                TransactionScreen(
                    viewModel,
                    true
                )
            }
        }

        composeTestRule.onNodeWithText("Force sync").assertIsDisplayed()
    }
}