package com.example.core.domain.model

import kotlinx.datetime.Instant

data class Transaction(
    val id: String,
    val accountId: String,
    val categoryId: String,
    val note: String,
    val amountCents: Long,
    val occurredAt: Instant,
    val isDeleted: Boolean = false
)
