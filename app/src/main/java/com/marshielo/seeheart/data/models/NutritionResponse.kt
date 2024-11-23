package com.marshielo.seeheart.data.models

data class NutritionResponse(
    val items: List<NutritionItem>
)

data class NutritionItem(
    val name: String,
    val calories: Float,
    val serving_size_g: Float,
    val fat_total_g: Float,
    val protein_g: Float,
    val carbohydrates_total_g: Float
)

