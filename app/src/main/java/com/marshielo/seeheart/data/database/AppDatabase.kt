package com.marshielo.seeheart.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedFoodEntity::class, WaterIntakeEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun waterDao(): WaterDao
    abstract fun savedFoodDao(): SavedFoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "seeheart_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
