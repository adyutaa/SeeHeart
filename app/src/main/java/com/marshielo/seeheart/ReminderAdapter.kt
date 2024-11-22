package com.marshielo.seeheart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(
    private val reminders: List<ReminderItem>,
    private val onCompleteClick: (ReminderItem) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.ivReminderIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvReminderTitle)
        val btnComplete: ImageButton = itemView.findViewById(R.id.btnMarkComplete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.imgIcon.setImageResource(reminder.iconResId) // Use iconResId instead of icon
        holder.tvTitle.text = reminder.description // Assuming you're displaying the description as the title
        holder.btnComplete.setOnClickListener { onCompleteClick(reminder) }
    }


    override fun getItemCount(): Int = reminders.size
}
