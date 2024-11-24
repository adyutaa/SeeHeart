package com.marshielo.seeheart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.marshielo.seeheart.data.database.AppDatabase
import com.marshielo.seeheart.ui.CalorieActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardActivity : AppCompatActivity(), SensorEventListener {

    // Sensor variables
    private var sensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var initialStepCount: Float = -1f // Tracks the initial step count value
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    // Water intake tracking
    private lateinit var waterCircularProgressBar: CircularProgressBar
    private lateinit var waterProgressValue: TextView
    private val dailyWaterTarget = 3000 // Daily water goal in ml

    // Step tracking
    private lateinit var stepsProgressValue: TextView
    private val dailyStepsTarget = 10000 // Daily step goal

    // Calorie tracking
    private lateinit var calorieProgressValue: TextView
    private lateinit var database: AppDatabase // Room Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)

        supportActionBar?.hide()

        // Initialize sensor for step counting
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Initialize Room Database
        database = AppDatabase.getDatabase(this)

        // Find views
        val waterCard = findViewById<CardView>(R.id.water_card)
        val calorieCard = findViewById<CardView>(R.id.calories_card)
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navReminder = findViewById<ImageView>(R.id.navReminder)
        val navNotes = findViewById<ImageView>(R.id.navNotes)
        waterCircularProgressBar = waterCard.findViewById(R.id.waterCircularProgressBar)
        waterProgressValue = waterCard.findViewById(R.id.waterProgressValue)
        stepsProgressValue = findViewById(R.id.stepsTaken)
        calorieProgressValue = findViewById(R.id.calorieProgressOnCard)

        // Water card navigation
        waterCard.setOnClickListener {
            val intent = Intent(this, WaterActivity::class.java)
            startActivity(intent)
        }

        // Calorie card navigation
        calorieCard.setOnClickListener {
            val intent = Intent(this, CalorieActivity::class.java)
            startActivity(intent)
        }

        // Bottom navigation
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        navReminder.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        navNotes.setOnClickListener {
            Toast.makeText(this, "You're already here!", Toast.LENGTH_SHORT).show()
        }

        // Initialize water intake UI
        initializeWaterIntake()

        // Fetch calorie data
        fetchTotalCalories()
    }

    override fun onResume() {
        super.onResume()

        // Register sensor listener
        if (sensor == null) {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Update water and calorie progress on resume
        updateWaterProgress()
        fetchTotalCalories()
    }

    override fun onPause() {
        super.onPause()

        // Unregister sensor listener
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            // Handle step counting
            initialStepCount = getSavedInitialStepCount()
            if (initialStepCount == -1f) {
                initialStepCount = event.values[0]
                saveInitialStepCount(initialStepCount)
            }
            val stepsTaken = (event.values[0] - initialStepCount).toInt()
            stepsProgressValue.text = "$stepsTaken steps"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("PedoSensor", "Sensor accuracy changed: $accuracy")
    }

    private fun initializeWaterIntake() {
        // Set max progress for water circular progress bar
        waterCircularProgressBar.progressMax = dailyWaterTarget.toFloat()

        // Update initial water progress
        updateWaterProgress()
    }

    private fun updateWaterProgress() {
        // Retrieve current water intake from SharedPreferences
        val sharedPreferences: SharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
        val currentWaterIntake = sharedPreferences.getInt("currentWaterIntake", 0)

        // Calculate percentage
        val percentage = if (dailyWaterTarget > 0) {
            (currentWaterIntake.toFloat() / dailyWaterTarget) * 100
        } else {
            0f
        }

        // Cap percentage at 100%
        val displayPercentage = if (percentage > 100f) 100f else percentage

        // Update CircularProgressBar and TextViews
        waterCircularProgressBar.setProgressWithAnimation(currentWaterIntake.toFloat(), 1000)
        waterProgressValue.text = "${displayPercentage.toInt()}%"
        val waterAmountTextView = findViewById<TextView>(R.id.tvWaterAmount)
        waterAmountTextView.text = "$currentWaterIntake ml"
    }

    private fun fetchTotalCalories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Query total calories from the database
                val totalCalories = database.savedFoodDao().getAllSavedFood().sumOf { it.calories.toInt() }
                withContext(Dispatchers.Main) {
                    // Update calorie progress value
                    calorieProgressValue.text = "$totalCalories Kcal"
                }
            } catch (e: Exception) {
                Log.e("CardActivity", "Error fetching calories: ${e.message}")
            }
        }
    }

    private fun saveInitialStepCount(value: Float) {
        val sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putFloat("initialStepCount", value).apply()
    }

    private fun getSavedInitialStepCount(): Float {
        val sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("initialStepCount", -1f)
    }
}
