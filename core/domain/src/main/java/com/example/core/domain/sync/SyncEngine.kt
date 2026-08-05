package com.example.core.domain.sync

interface SyncEngine {
    val entityName: String
    suspend fun sync(): SyncResult
}

sealed interface SyncResult {
    data object Success : SyncResult
    data class Failure(val error: Throwable) : SyncResult
}