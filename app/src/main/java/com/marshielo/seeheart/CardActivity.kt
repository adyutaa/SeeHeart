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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class CardActivity : AppCompatActivity(), SensorEventListener {

    private var sensor: Sensor? = null
    private var sensorManager: SensorManager? = null
    private var initialStepCount: Float = -1f // To track the first step count value
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    // Variabel untuk Water Intake
    private lateinit var waterCircularProgressBar: CircularProgressBar
    private lateinit var waterProgressValue: TextView
    private val dailyWaterTarget = 3000 // Target harian dalam ml

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)

        supportActionBar?.hide()

        // Inisialisasi Sensor Langkah
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Cari CardView dengan id water_card
        val waterCard = findViewById<CardView>(R.id.water_card)

        // Cari CircularProgressBar dan TextView di dalam water_card
        waterCircularProgressBar = waterCard.findViewById(R.id.waterCircularProgressBar)
        waterProgressValue = waterCard.findViewById(R.id.waterProgressValue)

        // Set onClickListener pada water_card
        waterCard.setOnClickListener {
            // Pindah ke WaterActivity
            val intent = Intent(this, WaterActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi tampilan Water Intake
        initializeWaterIntake()
    }

    private fun initializeWaterIntake() {
        // Set max progress untuk CircularProgressBar
        waterCircularProgressBar.progressMax = dailyWaterTarget.toFloat()

        // Update tampilan awal
        updateWaterProgress()
    }

    override fun onResume() {
        super.onResume()
        if (sensor == null) {
            Toast.makeText(this, "Sensor tidak ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // Update Water Progress setiap kali activity resume
        updateWaterProgress()
    }

    override fun onPause() {
        super.onPause()
        // Unregister the sensor listener
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val sensorCardText = findViewById<TextView>(R.id.stepsTaken)
            initialStepCount = getSavedInitialStepCount()
            if (initialStepCount == -1f) {
                // Save the first step count as the initial value
                initialStepCount = event.values[0]
                saveInitialStepCount(initialStepCount)
            }
            // Calculate the steps taken in this session
            val stepsTaken = (event.values[0] - initialStepCount).toInt()
            sensorCardText.text = "Steps: $stepsTaken"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Log perubahan akurasi sensor
        Log.d("PedoSensor", "Sensor accuracy changed: $accuracy")
    }

    // Fungsi untuk menyimpan initial step count
    private fun saveInitialStepCount(value: Float) {
        val sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("initialStepCount", value)
        editor.apply()
    }

    // Fungsi untuk mengambil initial step count
    private fun getSavedInitialStepCount(): Float {
        val sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("initialStepCount", -1f)
    }

    // Fungsi untuk memperbarui tampilan Water Progress
    private fun updateWaterProgress() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("WaterPrefs", Context.MODE_PRIVATE)
        val currentWaterIntake = sharedPreferences.getInt("currentWaterIntake", 0)

        // Hitung persentase
        val percentage = if (dailyWaterTarget > 0) {
            (currentWaterIntake.toFloat() / dailyWaterTarget) * 100
        } else {
            0f
        }

        // Batasi persentase hingga 100%
        val displayPercentage = if (percentage > 100f) 100f else percentage

        // Update CircularProgressBar
        waterCircularProgressBar.setProgressWithAnimation(currentWaterIntake.toFloat(), 1000)

        // Update TextView
        waterProgressValue.text = "${displayPercentage.toInt()}%"
    }

}
