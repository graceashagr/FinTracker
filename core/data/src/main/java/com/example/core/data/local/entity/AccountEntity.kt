package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.domain.model.Account
import com.example.core.domain.model.AccountType
import com.google.firebase.firestore.DocumentSnapshot

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
    type = AccountType.valueOf(type),
    isDeleted = isDeleted
)

fun AccountEntity.toFirestoreMap() : Map<String, Any> = mapOf(
    "id" to id,
    "name" to name,
    "type" to type,
    "updateAt" to updatedAt,
    "isDeleted" to isDeleted
)

fun DocumentSnapshot.toAccountEntity() : AccountEntity? {
    return try {
        AccountEntity(
            id = getString("id") ?: id,
            name = getString("name") ?: return null,
            type = getString("type") ?: return null,
            updatedAt = getLong("updatedAt") ?: return null,
            isDeleted = getBoolean("isDeleted") ?: false,
            syncStatus = "SYNCED"
        )
    } catch (e: Exception) {
        null
    }
}

