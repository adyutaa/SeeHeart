package com.marshielo.seeheart

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marshielo.seeheart.data.database.AppDatabase
import com.marshielo.seeheart.data.database.WaterIntakeEntity
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WaterActivity : AppCompatActivity() {

    private var currentWaterIntake = 0
    private val dailyTarget = 3000
    private lateinit var database: AppDatabase
    private lateinit var adapter: HistoryAdapter
    private val historyList = mutableListOf<WaterIntakeEntity>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.water_intake)

        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularWaterProgressBar)
        val tvProgress = findViewById<TextView>(R.id.tvWaterIntakeProgress)
        val btnDrinkWater = findViewById<Button>(R.id.btnDrinkWater)
        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        val btnReset = findViewById<Button>(R.id.btnReset)

        // Initialize RecyclerView and adapter
        adapter = HistoryAdapter(historyList)
        rvHistory.adapter = adapter
        rvHistory.layoutManager = LinearLayoutManager(this)

        // Initialize database
        database = AppDatabase.getDatabase(this)

        // Load daily data from the database
        loadDailyData()

        // Set max progress for the circular progress bar
        circularProgressBar.progressMax = dailyTarget. toFloat()

        btnDrinkWater.setOnClickListener {
            // Add 300 ml to the current intake
            val intake = 300
            currentWaterIntake += intake
            if (currentWaterIntake > dailyTarget) currentWaterIntake = dailyTarget

            // Get the current timestamp and format it
            val timestamp = System.currentTimeMillis()
            val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = sdfDate.format(Date(timestamp))
            val time = sdfTime.format(Date(timestamp))

            // Save the new intake to the database
            lifecycleScope.launch {
                database.waterDao().insertWaterIntake(
                    WaterIntakeEntity(date = date, time = time, intake = intake)
                )
                loadDailyData() // Refresh history and progress
            }

            // Update the UI
            circularProgressBar.setProgressWithAnimation(currentWaterIntake.toFloat(), 1000)
            tvProgress.text = "$currentWaterIntake / $dailyTarget ml"

            // Simpan total asupan air ke SharedPreferences
            val sharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("currentWaterIntake", currentWaterIntake)
            editor.apply()


        }

        btnReset.setOnClickListener {
            // Reset progress and clear the database for today
            currentWaterIntake = 0
            historyList.clear()
            adapter.notifyDataSetChanged()

            circularProgressBar.setProgressWithAnimation(0f, 1000)
            tvProgress.text = "0 / $dailyTarget ml"

            val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdfDate.format(Date())

            // Reset total asupan air di SharedPreferences
            val sharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("currentWaterIntake", 0) // Reset to 0
            editor.apply()


            lifecycleScope.launch {
                database.waterDao().deleteWaterIntakeByDate(date)
            }
        }
    }

    private fun loadDailyData() {
        // Load water intake for the current date from the database
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdfDate.format(Date())

        lifecycleScope.launch {
            val intakeList = database.waterDao().getWaterIntakeByDate(date)
            currentWaterIntake = intakeList.sumOf { it.intake }

//            // Update history list
//            historyList.clear()
//            val newEntries: MutableList<String> = intakeList.map { intake ->
//                "Drank ${intake.intake} ml at ${intake.time}"
//            }.toMutableList() /// possible error cause
//            adapter.notifyDataSetChanged()
            // Update history list with proper data from the database
            historyList.clear()
            historyList.addAll(intakeList) // Ensure the list is populated directly
            adapter.notifyDataSetChanged() // Notify the adapter about the data changes

            // Update progress
            findViewById<CircularProgressBar>(R.id.circularWaterProgressBar)
                .setProgressWithAnimation(currentWaterIntake.toFloat(), 1000)
            findViewById<TextView>(R.id.tvWaterIntakeProgress).text =
                "$currentWaterIntake / $dailyTarget ml"
        }
    }
}
