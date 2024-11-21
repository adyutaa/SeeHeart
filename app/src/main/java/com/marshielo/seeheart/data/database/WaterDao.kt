package com.marshielo.seeheart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface WaterDao {
    @Insert
    suspend fun insertWaterIntake(waterIntake: WaterIntakeEntity)

    @Query("SELECT * FROM water_intake WHERE date= :date")
    suspend fun getWaterIntakeByDate(date: String): List<WaterIntakeEntity>

    @Query("DELETE FROM water_intake WHERE date = :date")
    suspend fun deleteWaterIntakeByDate(date: String)

}