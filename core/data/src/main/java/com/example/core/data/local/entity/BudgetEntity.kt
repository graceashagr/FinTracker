package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.core.domain.model.Budget
import com.google.firebase.firestore.DocumentSnapshot

@Entity(tableName = "budgets",
        foreignKeys = [ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("categoryId")]
)
data class BudgetEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val month: String,
    val limitCents: Long,
    val updatedAt: Long,
    val isDeleted: Boolean,
    val syncStatus: String
)

fun BudgetEntity.toDomain() = Budget(
    id = id,
    categoryId = categoryId,
    month = month,
    limitCents = limitCents,
    isDeleted = isDeleted
)

fun BudgetEntity.toFirestoreMap(): Map<String, Any> = mapOf(
    "id" to id,
    "categoryId" to categoryId,
    "month" to month,
    "limitCents" to limitCents,
    "updatedAt" to updatedAt,
    "isDeleted" to isDeleted
)

fun DocumentSnapshot.toBudgetEntity() : BudgetEntity? {
    return try {
        BudgetEntity(
            id = getString("id") ?: id,
            categoryId = getString("categoryId") ?: return null,
            month = getString("month") ?: return null,
            limitCents = getLong("limitCents") ?: return null,
            updatedAt = getLong("updatedAt") ?: return null,
            isDeleted = getBoolean("isDeleted") ?: false,
            syncStatus = "SYNCED"
        )
    } catch (e: Exception) {
        null
    }
}