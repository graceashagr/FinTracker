package com.example.feature.accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Account
import com.example.core.domain.model.AccountType
import com.example.core.domain.repository.AccountRepository
import com.example.core.domain.sync.SyncScheduler
import com.example.core.domain.usecase.AddAccountUseCase
import com.example.core.domain.usecase.DeleteAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountUiState(
    val accounts: List<Account> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val addAccountUseCase: AddAccountUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val accountRepository: AccountRepository,
    private val syncScheduler: SyncScheduler
) : ViewModel() {

    val accountUiState : StateFlow<AccountUiState> = accountRepository.observeAccounts()
        .map { accounts ->
            AccountUiState(
                accounts = accounts,
                isLoading = false
            )
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AccountUiState()
        )

    fun addAccount(name: String, type: AccountType) {
        viewModelScope.launch {
            addAccountUseCase(name, type)
        }
    }

    fun deleteAccount(accountId: String){
        viewModelScope.launch {
            deleteAccountUseCase(accountId)
        }
    }
}