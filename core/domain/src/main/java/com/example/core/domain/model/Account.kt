package com.example.core.domain.model

data class Account(
    val id: String,
    val name: String,
    val type: String,
    val isDeleted: Boolean = false
)
