package com.example.core.domain.model

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val isDeleted: Boolean = false
)

enum class AccountType { CHECKING, CASH, CREDIT_CARD }