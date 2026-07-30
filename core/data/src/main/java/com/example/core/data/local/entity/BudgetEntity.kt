package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.core.domain.model.Budget

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
