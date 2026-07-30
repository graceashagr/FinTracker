package com.example.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.core.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun observeAll() : Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type=:type")
    fun observeByType(
        type: String
    ) : Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsert(category: CategoryEntity)


}