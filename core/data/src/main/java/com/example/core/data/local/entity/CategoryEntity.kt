package com.example.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String, // INCOME/EXPENSE
    val icon: String,
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    icon = icon
)