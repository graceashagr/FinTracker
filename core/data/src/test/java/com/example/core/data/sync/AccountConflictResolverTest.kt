package com.example.core.data.sync

import com.example.core.data.local.entity.AccountEntity
import com.example.core.domain.model.Account
import com.example.core.domain.model.AccountType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AccountConflictResolverTest {
    private val conflictResolver = AccountConflictResolver()

    private val account = AccountEntity(
        id = "acc-1",
        name = "Test Account",
        type = "CHECKING",
        isDeleted = false,
        updatedAt = 1000L,
        syncStatus = "PENDING"
    )

    @Test
    fun `when local is null,incoming is accepted as-is and marked synced`() {
        val incoming = account.copy(name = "Remote Account")

        val result = conflictResolver.merge(local = null, incoming = incoming)

        assertEquals(result.name, "Remote Account")
        assertEquals(result.syncStatus, "SYNCED")
    }

    @Test
    fun `when incoming is newer, incoming wins`() {
        val incoming = account.copy(name = "Remote Account", updatedAt = 2000L)
        val local = account.copy(name = "Local Account", updatedAt = 1000L)

        val result = conflictResolver.merge(local = local, incoming = incoming)
        assertEquals(result.name, "Remote Account")
        assertEquals(result.syncStatus, "SYNCED")
    }

    @Test
    fun `when local is newer, local wins and stays pending`() {
        val local = account.copy(name = "new local", updatedAt = 2000L)
        val incoming = account.copy(name = "stale remote", updatedAt = 1000L)
        val result = conflictResolver.merge(local, incoming)
        assertEquals("new local", result.name)
        assertEquals("PENDING", result.syncStatus)
    }

    @Test
    fun `when local is deleted, local wins regardless of incoming timestamp`() {
        val local = account.copy(isDeleted = true, updatedAt = 1000L)
        val incoming = account.copy(updatedAt = 9999L)
        assertTrue(conflictResolver.merge(local, incoming).isDeleted)
    }

    @Test
    fun `when incoming is deleted and local is not, incoming delete propagates`() {
        val local = account.copy(isDeleted = false, updatedAt = 1000L)
        val incoming = account.copy(isDeleted = true, updatedAt = 2000L)
        assertTrue(conflictResolver.merge(local, incoming).isDeleted)
    }
}