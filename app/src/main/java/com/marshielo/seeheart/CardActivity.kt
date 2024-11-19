package com.marshielo.seeheart

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CardActivity : AppCompatActivity(), SensorEventListener{

    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null
    private var initialStepCount: Float = -1f // To track the first step count value
    private val ACTIVITY_RECOGNITION_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card)

        supportActionBar?.hide()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val bmi = findViewById<ImageView>(R.id.imageBMI)

        bmi.setOnClickListener{
            val i= Intent(this, BMIActivity::class.java)
            startActivity(i)
        }
    }

    private fun requestActivityRecognitionPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        } else {
            initializeStepCounter()
        }
    }

    private fun saveInitialStepCount(value: Float) {
        val sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("initialStepCount", value)
        editor.apply()
    }

    private fun getSavedInitialStepCount(): Float {
        val sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("initialStepCount", -1f)
    }

    private fun initializeStepCounter() {
        if (sensor == null) {
            val sensorCardText = findViewById<TextView>(R.id.stepsTaken)
            sensorCardText.text = "Step Counter Sensor Not Available"
        }
    }

    override fun onResume() {
        super.onResume()
        if(sensor == null) {
            Toast.makeText(this,"Sensor not Found", Toast.LENGTH_SHORT).show()
        } else {
            //   sensorManager?.registerListener(this,sensor, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
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
        // Example: Log sensor accuracy changes
        Log.d("PedoSensor", "Sensor accuracy changed: $accuracy")
    }
}