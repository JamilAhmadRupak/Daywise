package com.daywise.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daywise.app.data.model.CustomCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM custom_categories ORDER BY created_at ASC")
    fun getAllCustomCategoriesFlow(): Flow<List<CustomCategoryEntity>>

    @Query("SELECT * FROM custom_categories ORDER BY created_at ASC")
    suspend fun getAllCustomCategories(): List<CustomCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CustomCategoryEntity)

    @Query("DELETE FROM custom_categories WHERE name = :name")
    suspend fun deleteCategory(name: String)
}

