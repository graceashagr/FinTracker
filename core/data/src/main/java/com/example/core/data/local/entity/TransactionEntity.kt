package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.domain.model.Transaction
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.datetime.Instant

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val accountId: String,
    val categoryId: String,
    val note: String,
    val amountCents: Long,
    val occurredAt: Long,
    val updatedAt: Long,
    val isDeleted: Boolean = false,
    val syncStatus: String,
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    accountId = accountId,
    categoryId = categoryId,
    note = note,
    amountCents = amountCents,
    occurredAt = Instant.fromEpochMilliseconds(occurredAt),
    isDeleted = isDeleted
)

fun TransactionEntity.toFirestoreMap(): Map<String, Any> = mapOf(
    "id" to id,
    "accountId" to accountId,
    "categoryId" to categoryId,
    "note" to note,
    "amountCents" to amountCents,
    "occurredAt" to occurredAt,
    "updatedAt" to updatedAt,
    "isDeleted" to isDeleted
)

fun DocumentSnapshot.toTransactionEntity(): TransactionEntity? {
    return try {
        TransactionEntity(
            id = getString("id") ?: id,   // fall back to Firestore's own doc id if the field is missing
            accountId = getString("accountId") ?: return null,
            categoryId = getString("categoryId") ?: return null,
            amountCents = getLong("amountCents") ?: return null,
            note = getString("note").orEmpty(),
            occurredAt = getLong("occurredAt") ?: return null,
            updatedAt = getLong("updatedAt") ?: return null,
            isDeleted = getBoolean("isDeleted") ?: false,
            syncStatus = "SYNCED"   // anything just pulled from remote is, by definition, synced
        )
    } catch (e: Exception) {
        null   // malformed remote doc — skip it rather than crash the whole sync pass
    }
}