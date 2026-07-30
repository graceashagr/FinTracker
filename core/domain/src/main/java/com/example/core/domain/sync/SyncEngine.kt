package com.example.core.domain.sync

interface SyncEngine {
    suspend fun syncTransactions(): SyncResult
}

sealed interface SyncResult {
    data object Success : SyncResult
    data class Failure(val error: Throwable) : SyncResult
}