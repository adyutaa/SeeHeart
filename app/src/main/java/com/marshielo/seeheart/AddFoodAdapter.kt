import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.data.models.NutritionItem
import com.marshielo.seeheart.databinding.ItemFoodCardBinding

class FoodAdapter(private val onFoodClick: (NutritionItem) -> Unit) :
    ListAdapter<NutritionItem, FoodAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FoodViewHolder(private val binding: ItemFoodCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(food: NutritionItem) {
            binding.tvFoodName.text = food.name
            binding.tvFoodCalories.text = "${food.calories} Kcal"
            binding.tvServingSize.text = "Serving: ${food.serving_size_g}g"
            binding.tvMacros.text = "F: ${food.fat_total_g}g | P: ${food.protein_g}g | C: ${food.carbohydrates_total_g}g"

            itemView.setOnClickListener { onFoodClick(food) }
        }
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<NutritionItem>() {
        override fun areItemsTheSame(oldItem: NutritionItem, newItem: NutritionItem): Boolean {
            // Define logic to compare if two items are the same
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: NutritionItem, newItem: NutritionItem): Boolean {
            // Define logic to check if contents of two items are the same
            return oldItem == newItem
        }
    }
}
