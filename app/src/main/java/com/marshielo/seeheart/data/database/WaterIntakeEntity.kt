package com.marshielo.seeheart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "dd/MM/yyyy"
    val time: String, // "HH:mm:ss"
    val intake: Int
)

data class WeeklyIntakeEntity(
    val date: String,
    val totalIntake: Int
)
