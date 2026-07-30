package com.example.core.data.sync

import com.example.core.data.local.entity.TransactionEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class TransactionConflictResolverTest {

    private val resolver = TransactionConflictResolver()

    private fun transaction(
        id: String = "txn-1",
        note: String = "test",
        updatedAt: Long = 1000L,
        isDeleted: Boolean = false,
        syncStatus: String = "PENDING"
    ) = TransactionEntity(
        id = id, accountId = "acc-1", categoryId = "cat-1",
        amountCents = 500, note = note, occurredAt = 1000L,
        updatedAt = updatedAt, isDeleted = isDeleted, syncStatus = syncStatus
    )

    @Test
    fun `when local is null, incoming is accepted as-is and marked synced`() {
        val incoming = transaction(note = "remote note", syncStatus = "PENDING")

        val result = resolver.merge(local = null, incoming = incoming)

        assertEquals("SYNCED", result.syncStatus)
        assertEquals("remote note", result.note)
    }

    @Test
    fun `when incoming is newer, incoming wins`(){
        val incoming = transaction(note = "remote note", updatedAt = 2000L)
        val local = transaction(note = "local note", updatedAt = 1000L)

        val result = resolver.merge(local = local, incoming = incoming)

        assertEquals("SYNCED", result.syncStatus)
        assertEquals("remote note", result.note)
    }

    @Test
    fun `when local is newer, local wins and stays pending`() {
        val local = transaction(note = "new local", updatedAt = 2000L, syncStatus = "PENDING")
        val incoming = transaction(note = "stale remote", updatedAt = 1000L)

        val result = resolver.merge(local, incoming)

        assertEquals("new local", result.note)
        assertEquals("PENDING", result.syncStatus)
    }

    @Test
    fun `when local is deleted, local winds regardless of incoming`() {
        val local = transaction(note = "deleted local", isDeleted = true, updatedAt = 2000L)
        val incoming = transaction(note = "incoming", updatedAt = 3000L)

        val result = resolver.merge(local, incoming)

        assertEquals("deleted local", result.note)
        assertEquals(true, result.isDeleted)
    }

    @Test
    fun `when incoming is deleted, incoming wins regardless of local`() {
        val local = transaction(note = "local", updatedAt = 3000L)
        val incoming = transaction(note = "deleted remote", isDeleted = true, updatedAt = 4000L)

        val result = resolver.merge(local, incoming)

        assertEquals("deleted remote", result.note)
        assertEquals(true, result.isDeleted)
        assertEquals("SYNCED", result.syncStatus)  // incoming is deleted, so we mark it synced
    }
}