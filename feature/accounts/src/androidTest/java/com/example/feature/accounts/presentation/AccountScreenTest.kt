package com.example.feature.accounts.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.core.data.repository.AccountRepositoryImpl
import com.example.core.domain.repository.AccountRepository
import com.example.core.domain.usecase.AddAccountUseCase
import com.example.core.domain.usecase.DeleteAccountUseCase
import com.example.core.testing.fake.FakeAccountDao
import com.example.core.ui.theme.FinTrackerTheme
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AccountScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: AccountViewModel
    private lateinit var fakeDao: FakeAccountDao
    private lateinit var repository: AccountRepository

    @Before
    fun setUp(){
        fakeDao = FakeAccountDao()
        repository = AccountRepositoryImpl(fakeDao)
        viewModel = AccountViewModel(
            addAccountUseCase = AddAccountUseCase(repository, mockk(relaxed = true)),
            deleteAccountUseCase = DeleteAccountUseCase(repository, mockk(relaxed = true)),
            accountRepository = repository,
            syncScheduler = mockk(relaxed = true),
        )
    }

    @Test
    fun emptyState_NoAccounts(){
        composeRule.setContent {
            FinTrackerTheme {
                AccountScreen(
                    viewModel = viewModel,
                )
            }
        }

        composeRule.onNodeWithText("CHECKING").assertDoesNotExist()
    }

    @Test
    fun addingAccountViaDialog_showsInList(){
        composeRule.setContent {
            FinTrackerTheme {
                AccountScreen(
                    viewModel = viewModel,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Add Account").performClick()
        composeRule.onNodeWithTag("accountNameInput").performTextInput("CHECKING")
        composeRule.onNodeWithTag("accountTypeInput").performClick()
        composeRule.onNodeWithTag("accountTypeDropdown").assertIsDisplayed()
        composeRule.onNodeWithTag("accountTypeItem_CHECKING").performClick()
        composeRule.onNodeWithText("Add Account").performClick()
        composeRule.waitUntil(timeoutMillis = 2000){
            composeRule.onAllNodesWithText("CHECKING").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("CHECKING").assertIsDisplayed()
    }

    @Test
    fun cancellingDialog_doesnotAddAccount(){
        composeRule.setContent {
            FinTrackerTheme {
                AccountScreen(
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithContentDescription("Add Account").performClick()
        composeRule.onNodeWithTag("accountNameInput").performTextInput("CHECKING")
        composeRule.onNodeWithTag("accountTypeInput").performClick()
        composeRule.onNodeWithTag("accountTypeDropdown").assertIsDisplayed()
        composeRule.onNodeWithTag("accountTypeItem_CHECKING").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.waitUntil(timeoutMillis = 2000){
            composeRule.onAllNodesWithText("CHECKING").fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithText("CHECKING").assertDoesNotExist()
    }

    @Test
    fun deletingAccount_RemovesFromList(){
        composeRule.setContent {
            FinTrackerTheme {
                AccountScreen(
                    viewModel = viewModel
                )
            }
        }

        composeRule.onNodeWithContentDescription("Add Account").performClick()
        composeRule.onNodeWithTag("accountNameInput").performTextInput("CHECKING")
        composeRule.onNodeWithTag("accountTypeInput").performClick()
        composeRule.onNodeWithTag("accountTypeDropdown").assertIsDisplayed()
        composeRule.onNodeWithTag("accountTypeItem_CHECKING").performClick()
        composeRule.onNodeWithText("Add Account").performClick()
        composeRule.waitUntil(timeoutMillis = 2000){
            composeRule.onAllNodesWithText("CHECKING").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("CHECKING").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Delete Account").performClick()
        composeRule.waitUntil(timeoutMillis = 2000){
            composeRule.onAllNodesWithText("CHECKING").fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithText("CHECKING").assertDoesNotExist()

    }
}