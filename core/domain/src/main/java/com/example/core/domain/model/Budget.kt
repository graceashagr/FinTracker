package com.example.core.domain.model

data class Budget(
    val id: String,
    val categoryId: String,
    val month: String,
    val limitCents: Long,
    val isDeleted: Boolean = false
)
