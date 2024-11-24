package com.marshielo.seeheart.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.R
import com.marshielo.seeheart.data.database.AppDatabase
import com.marshielo.seeheart.data.database.SavedFoodEntity
import com.marshielo.seeheart.ui.adapter.FoodHistoryAdapter
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CalorieActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var adapter: FoodHistoryAdapter
    private val historyList = mutableListOf<SavedFoodEntity>()
    private var currentCalorieIntake = 0
    private val dailyTarget = 2300
    private lateinit var calorieProgressBar: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calorie)

        val calorieCount = findViewById<TextView>(R.id.calorieCount)
        val recyclerView = findViewById<RecyclerView>(R.id.rvFoodHistory)
        val addFoodButton = findViewById<TextView>(R.id.tvAddFood)
        calorieProgressBar = findViewById(R.id.calorieProgress)

        // Navigate to AddFoodActivity
        addFoodButton.setOnClickListener {
            val intent = Intent(this, AddFoodActivity::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView
        adapter = FoodHistoryAdapter(historyList) { food ->
            // Handle food item click (e.g., deletion)
            deleteFoodItem(food)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Room Database
        database = AppDatabase.getDatabase(this)

        // Load today's food history
        loadDailyData()
    }

    override fun onResume() {
        super.onResume()
        loadDailyData()
    }

    private fun loadDailyData() {
        val currentDate = getCurrentDate()

        lifecycleScope.launch {
            // Query the database for today's food history
            val dailyFoodItems = database.savedFoodDao().getAllSavedFood()

            // Calculate total calorie intake
            currentCalorieIntake = dailyFoodItems.sumOf { it.calories.toInt() }

            // Update the calorie count in the UI
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.calorieCount).text = "$currentCalorieIntake Kcal"

                // Update the RecyclerView with new data
                historyList.clear()
                historyList.addAll(dailyFoodItems)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteFoodItem(food: SavedFoodEntity) {
        lifecycleScope.launch {
            // Remove the item from the database
            database.savedFoodDao().deleteFood(food)

            // Update the UI and calorie count
            withContext(Dispatchers.Main) {
                historyList.remove(food)
                adapter.notifyDataSetChanged()

                currentCalorieIntake -= food.calories.toInt()
                findViewById<TextView>(R.id.calorieCount).text = "$currentCalorieIntake Kcal"

                Toast.makeText(this@CalorieActivity, "${food.name} removed from history", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun updateCalorieUI() {
        val calorieCountTextView = findViewById<TextView>(R.id.calorieCount)
        calorieCountTextView.text = "$currentCalorieIntake Kcal"

        // Calculate the progress percentage
        val progressPercentage = (currentCalorieIntake.toFloat() / dailyTarget) * 100

        // Animate the CircularProgressBar
        calorieProgressBar.setProgressWithAnimation(progressPercentage.coerceAtMost(100f), 1500) // 1500ms animation duration
    }
}
