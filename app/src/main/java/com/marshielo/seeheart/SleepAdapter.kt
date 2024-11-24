package com.marshielo.seeheart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.data.database.SleepDataEntity
import java.time.format.DateTimeFormatter

class SleepLogAdapter : ListAdapter<SleepDataEntity, SleepLogAdapter.SleepLogViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SleepDataEntity>() {
            override fun areItemsTheSame(oldItem: SleepDataEntity, newItem: SleepDataEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SleepDataEntity, newItem: SleepDataEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sleep_log_items, parent, false)
        return SleepLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: SleepLogViewHolder, position: Int) {
        val sleepLog = getItem(position)
        holder.bind(sleepLog)
    }

    class SleepLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeTextView: TextView = itemView.findViewById(R.id.start_time_text)
        private val endTimeTextView: TextView = itemView.findViewById(R.id.end_time_text)
        private val durationTextView: TextView = itemView.findViewById(R.id.duration_text)
        private val dayTextView: TextView = itemView.findViewById(R.id.day_text) // Add a TextView for the day

        private val dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val dayFormatter = DateTimeFormatter.ofPattern("EEEE") // Day of the week

        fun bind(sleepData: SleepDataEntity) {
            // Format the LocalDateTime for display
            val startTimeFormatted = sleepData.startTime.format(dateTimeFormatter)
            val endTimeFormatted = sleepData.endTime.format(dateTimeFormatter)
            val dayOfWeek = sleepData.startTime.format(dayFormatter)

            // Update TextViews with formatted date, time, and day
            startTimeTextView.text = "Start: $startTimeFormatted"
            endTimeTextView.text = "End: $endTimeFormatted"
            durationTextView.text = "Duration: %.2f hours".format(sleepData.durationInHours)
            dayTextView.text = "$dayOfWeek"
        }
    }

}
