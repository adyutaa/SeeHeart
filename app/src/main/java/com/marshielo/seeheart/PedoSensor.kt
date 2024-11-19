package com.marshielo.seeheart

import android.content.Context
import android.content.Intent
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

class PedoSensor : AppCompatActivity(), SensorEventListener {

    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pedo_sensor)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
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
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val stepsTaken = event.values[0].toInt() // Cast to `Int` if needed.
            val stepsTextView = findViewById<TextView>(R.id.stepsTaken)
            stepsTextView.text = stepsTaken.toString()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Example: Log sensor accuracy changes
        Log.d("PedoSensor", "Sensor accuracy changed: $accuracy")
    }



}