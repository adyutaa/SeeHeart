package com.marshielo.seeheart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface SavedFoodDao {

    @Insert
    suspend fun insertFood(food: SavedFoodEntity)

    @Query("SELECT * FROM saved_food_items ORDER BY id DESC")
    suspend fun getAllSavedFood(): List<SavedFoodEntity>

    @Delete
    suspend fun deleteFood(food: SavedFoodEntity)
}