package com.marshielo.seeheart



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.SleepSegmentEvent
import com.marshielo.seeheart.data.database.AppDatabase
import com.marshielo.seeheart.data.database.SleepDataEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SleepReceiver : BroadcastReceiver() {

    // Define the DateTimeFormatter at the class level
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun onReceive(context: Context, intent: Intent) {
        if (SleepSegmentEvent.hasEvents(intent)) {
            val events = SleepSegmentEvent.extractEvents(intent)
            events?.forEach { event ->
                // Convert event start and end times to formatted strings
                val startTime = Instant.ofEpochMilli(event.startTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .format(dateTimeFormatter)

                val endTime = Instant.ofEpochMilli(event.endTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .format(dateTimeFormatter)

                // Assuming SleepDataEntity expects LocalDateTime
                val sleepDataEntity = SleepDataEntity(
                    startTime = LocalDateTime.parse(startTime, dateTimeFormatter),
                    endTime = LocalDateTime.parse(endTime, dateTimeFormatter)
                )

                // Save data to the database
                val db = AppDatabase.getDatabase(context)
                CoroutineScope(Dispatchers.IO).launch {
                    db.sleepDataDao().insertSleepData(sleepDataEntity)
                }
            }
        }
    }
}

