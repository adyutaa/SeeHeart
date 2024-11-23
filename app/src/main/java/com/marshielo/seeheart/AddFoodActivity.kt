package com.marshielo.seeheart.ui

import FoodAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.data.api.ApiClient
import com.marshielo.seeheart.data.database.AppDatabase
import com.marshielo.seeheart.data.database.SavedFoodEntity
import com.marshielo.seeheart.data.models.NutritionItem
import com.marshielo.seeheart.data.models.NutritionResponse
import com.marshielo.seeheart.databinding.ActivityAddFoodBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var database: AppDatabase

    private var isLoading = false
    private var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setup db
        database = AppDatabase.getDatabase(this)

        // Setup back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Setup RecyclerView and Adapter
        setupRecyclerView()

        // Setup SearchView functionality
        setupSearchView()
    }

    private fun setupRecyclerView() {
        // Initialize FoodAdapter with click listener
        foodAdapter = FoodAdapter { foodItem ->
            onFoodItemSelected(foodItem)
        }

        // Set up RecyclerView
        binding.rvFoodList.apply {
            layoutManager = LinearLayoutManager(this@AddFoodActivity)
            adapter = foodAdapter
        }
    }

    private fun setupSearchView() {
        binding.foodSearchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lastQuery = it
                    foodAdapter.submitList(emptyList()) // Clear current list
                    fetchFoodData(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false // Handle filtering only on submit
            }
        })
    }

    private fun fetchFoodData(query: String) {
        isLoading = true

        // Launch coroutine for network request
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make API call
                val response = ApiClient.nutritionApiService.getNutritionInfo(query)

                // Log the raw response for debugging
                Log.d("FetchFoodData", "Response: $response")

                withContext(Dispatchers.Main) {
                    if (response.items.isNotEmpty()) {
                        // Clear current list and add new items
                        val newFoodItems = response.items
                        foodAdapter.submitList(newFoodItems) // Replace the entire list
                    } else {
                        Toast.makeText(this@AddFoodActivity, "No food data found for \"$query\"", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddFoodActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                isLoading = false
            }
        }
    }


    private fun onFoodItemSelected(foodItem: NutritionItem) {
        // Save food yang dipilih
        CoroutineScope(Dispatchers.IO).launch{
            val savedFood = SavedFoodEntity(
                name = foodItem.name,
                calories = foodItem.calories,
                servingSize = foodItem.serving_size_g,
                fat = foodItem.fat_total_g,
                protein = foodItem.protein_g,
                carbs = foodItem.carbohydrates_total_g
            )
            database.savedFoodDao().insertFood(savedFood)

            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddFoodActivity, "${foodItem.name} telah ditambahkan.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
