package com.example.feature.transactions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.model.Transaction
import com.example.core.domain.repository.TransactionRepository
import com.example.core.domain.sync.SyncScheduler
import com.example.core.domain.usecase.AddTransactionUseCase
import com.example.core.domain.usecase.DeleteTransactionUseCase
import com.example.core.domain.usecase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


data class TransactionUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val pendingCount: Int = 0
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val transactionRepository: TransactionRepository,
    private val syncScheduler: SyncScheduler
): ViewModel() {

    val uiState : StateFlow<TransactionUiState> = combine(
        transactionRepository.observePendngTransactionCount(),
        transactionRepository.observeTransactions()
    ){ pendingCount , transaction ->
        TransactionUiState(
            transactions = transaction,
            isLoading = false,
            pendingCount = pendingCount
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TransactionUiState()
    )


    fun addClicked(accountId: String, categoryId: String, amount: Long, note: String){
       viewModelScope.launch {
           addTransactionUseCase(
               accountId,
               categoryId,
               amount,
               note
           )
       }
    }

    fun deleteClick(id : String){
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }

    fun updateClick(transaction: Transaction){
        viewModelScope.launch {
            updateTransactionUseCase(transaction)
        }
    }

    fun onForceSyncClick() {
        syncScheduler.triggerImmediateSync()
    }
}