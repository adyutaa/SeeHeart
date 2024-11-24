package com.marshielo.seeheart

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(
    private val reminders: List<ReminderItem>,
    private val onCompleteClick: (ReminderItem) -> Unit,
    private val onSaveReminders: () -> Unit // Callback untuk menyimpan data
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    // ViewHolder untuk item layout
    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.ivReminderIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvReminderTitle)
        val switchComplete: Switch = itemView.findViewById(R.id.switchMarkComplete)
        val tvCompletedStatus: TextView = itemView.findViewById(R.id.tvCompletedStatus) // Tambahan TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]

        // Atur icon dan teks
        holder.imgIcon.setImageResource(reminder.iconResId)
        holder.tvTitle.text = reminder.description

        // Set initial state of the switch dan TextView
        holder.switchComplete.isChecked = reminder.isCompleted
        holder.tvCompletedStatus.visibility = if (reminder.isCompleted) {
            View.VISIBLE
        } else {
            View.GONE
        }

        // Atur klik pada Switch
        holder.switchComplete.setOnCheckedChangeListener { _, isChecked ->
            reminder.isCompleted = isChecked // Update status di data model
            holder.tvCompletedStatus.visibility = if (isChecked) {
                View.VISIBLE
            } else {
                View.GONE
            }
            onSaveReminders() // Panggil callback untuk menyimpan data
        }
    }

    override fun getItemCount(): Int = reminders.size
}