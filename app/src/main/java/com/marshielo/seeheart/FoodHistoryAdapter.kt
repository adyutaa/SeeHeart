package com.marshielo.seeheart.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.R
import com.marshielo.seeheart.data.database.SavedFoodEntity

class FoodHistoryAdapter(
    private val foodList: List<SavedFoodEntity>,
    private val onItemClick: (SavedFoodEntity) -> Unit,
) : RecyclerView.Adapter<FoodHistoryAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_history, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem, onItemClick)
        holder.itemView.setOnClickListener { onItemClick(foodItem) }
    }

    override fun getItemCount(): Int = foodList.size

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodName: TextView = itemView.findViewById(R.id.tvFoodName)
        private val foodCalories: TextView = itemView.findViewById(R.id.tvFoodCalories)
        private val deleteButton: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(food: SavedFoodEntity, onItemClick: (SavedFoodEntity) -> Unit) {
            foodName.text = food.name
            foodCalories.text = "${food.calories} Kcal"

            deleteButton.setOnClickListener{
                onItemClick(food)
            }
        }
    }
}
