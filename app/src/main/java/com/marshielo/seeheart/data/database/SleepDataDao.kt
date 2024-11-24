package com.marshielo.seeheart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDataDao {

    @Insert
    suspend fun insertSleepData(sleepData: SleepDataEntity)

    @Query("SELECT * FROM sleep_data WHERE id = :id")
    suspend fun getSleepDataById(id: Int): SleepDataEntity?

    @Query("SELECT * FROM sleep_data")
    suspend fun getAllSleepData(): List<SleepDataEntity>

    @Query("SELECT * FROM sleep_data ORDER BY startTime DESC")
    fun getAllSleepDataFlow(): Flow<List<SleepDataEntity>>

}



