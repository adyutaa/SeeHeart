package com.marshielo.seeheart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_food_items")
data class SavedFoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Float,
    val servingSize: Float,
    val fat: Float,
    val protein: Float,
    val carbs: Float
)