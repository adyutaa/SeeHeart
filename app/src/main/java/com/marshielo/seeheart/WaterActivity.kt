package com.marshielo.seeheart

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.SimpleDateFormat
import java.util.Date


class WaterActivity : AppCompatActivity() {

    private var currentWaterIntake = 0
    private val dailyTarget = 3000
    private val historyList = mutableListOf<String>() // Daftar riwayat
    private lateinit var adapter: HistoryAdapter // Adapter untuk RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.water_intake)

        val circularProgressBar = findViewById<CircularProgressBar>(R.id.circularWaterProgressBar)
        val tvProgress = findViewById<TextView>(R.id.tvWaterIntakeProgress)
        val btnDrinkWater = findViewById<Button>(R.id.btnDrinkWater)
        val rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        val btnReset = findViewById<Button>(R.id.btnReset) // Menemukan tombol Reset

        // Atur RecyclerView
        adapter = HistoryAdapter(historyList)
        rvHistory.adapter = adapter
        rvHistory.layoutManager = LinearLayoutManager(this)

        // Load data yang tersimpan
        loadData()

        // Atur max progress
        circularProgressBar.progressMax = dailyTarget.toFloat()

        btnDrinkWater.setOnClickListener {
            // Tambah riwayat dan update progress
            currentWaterIntake += 300
            if (currentWaterIntake > dailyTarget) currentWaterIntake = dailyTarget

            // Mengonversi waktu ke format yang lebih mudah dibaca
            val timestamp = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val formattedDate = sdf.format(Date(timestamp))

            // Tambahkan riwayat
            val entry = "Drank 300 ml at $formattedDate"
            historyList.add(entry)
            adapter.notifyDataSetChanged()

            // Update UI
            circularProgressBar.setProgressWithAnimation(currentWaterIntake.toFloat(), 1000)
            tvProgress.text = "$currentWaterIntake / $dailyTarget ml"

            // Simpan data
            saveData()
        }

        btnReset.setOnClickListener {
            // Reset semua data
            currentWaterIntake = 0
            historyList.clear()
            adapter.notifyDataSetChanged()

            // Update UI
            circularProgressBar.setProgressWithAnimation(0f, 1000)
            tvProgress.text = "0 / $dailyTarget ml"

            // Hapus data yang tersimpan
            val sharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
        }
    }

    private fun saveData() {
        val sharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Simpan intake air dan riwayat
        editor.putInt("currentWaterIntake", currentWaterIntake)
        editor.putStringSet("historyList", historyList.toSet())
        editor.apply()
    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
        currentWaterIntake = sharedPreferences.getInt("currentWaterIntake", 0)
        historyList.clear()
        historyList.addAll(sharedPreferences.getStringSet("historyList", emptySet())!!)
    }
}

