package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.domain.model.Account

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // CASH/CHECKING/CREDIT_CARD
    val updatedAt: Long,
    val syncStatus: String,
    val isDeleted: Boolean
    )


fun AccountEntity.toDomain() = Account(
    id = id,
    name = name,
    type = type,
    isDeleted = isDeleted
)