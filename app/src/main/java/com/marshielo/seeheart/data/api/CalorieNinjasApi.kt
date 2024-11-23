package com.marshielo.seeheart.data.api

import com.marshielo.seeheart.data.models.NutritionResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface FoodApiService {
    @Headers("X-Api-Key: XZP7N1/ZViHppFTthD9WHA==bhdY4WfNFGkMGam9")
    @GET("v1/nutrition")
    suspend fun getNutritionInfo(@Query("query") query: String): NutritionResponse
}
