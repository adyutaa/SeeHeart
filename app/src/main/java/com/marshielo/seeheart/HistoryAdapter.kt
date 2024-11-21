package com.marshielo.seeheart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.data.database.WaterIntakeEntity

class HistoryAdapter(private val historyList: List<WaterIntakeEntity>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgWaterCup: ImageView = itemView.findViewById(R.id.imgWaterCup)
        val tvHistoryItem: TextView = itemView.findViewById(R.id.tvHistoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false) // Use the custom layout
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val record = historyList[position]
        holder.tvHistoryItem.text = "${record.time}: Drank ${record.intake} ml"
        holder.imgWaterCup.setImageResource(R.drawable.watercup) // Set the water cup icon
    }

    override fun getItemCount(): Int = historyList.size
}
