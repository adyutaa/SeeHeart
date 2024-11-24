package com.marshielo.seeheart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity(tableName = "sleep_data")
data class SleepDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: LocalDateTime, // Store as LocalDateTime
    val endTime: LocalDateTime // Store as LocalDateTime
) {
    val durationInHours: Double
        get() {
            // Calculate duration using LocalDateTime
            return ChronoUnit.SECONDS.between(startTime, endTime) / 3600.0
        }
}
