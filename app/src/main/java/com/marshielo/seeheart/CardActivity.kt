package com.marshielo.seeheart

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

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

////        val bmi = findViewById<ImageView>(R.id.imageBMI)
//
//        bmi.setOnClickListener{
//            val i= Intent(this, BMIActivity::class.java)
//            startActivity(i)
//        }

        // Barchart Water
        setupWaterBarChart()

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

    private fun setupWaterBarChart() {
        // Find the BarChart from the layout
        val barChart: BarChart = findViewById(R.id.waterBarChart)

        // Sample data for the bar chart
        val waterIntakeData = listOf(
            BarEntry(0f, 1.5f), // Day 1: 1.5 liters
            BarEntry(1f, 2f),   // Day 2: 2 liters
            BarEntry(2f, 2.3f), // Day 3: 2.3 liters
            BarEntry(3f, 1.8f), // Day 4: 1.8 liters
            BarEntry(4f, 2.1f), // Day 5: 2.1 liters
            BarEntry(5f, 2.5f), // Day 6: 2.5 liters
            BarEntry(6f, 1.7f)  // Day 7: 1.7 liters
        )
        /// kita harus bikin viewmodel sama adapter buat waterIntakeData --> database.


        // Create the dataset
        val dataSet = BarDataSet(waterIntakeData, "Water Intake (liters)")
        dataSet.color = resources.getColor(R.color.teal_200, null)
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = resources.getColor(R.color.black, null)

        // Create BarData and set it to the chart
        val barData = BarData(dataSet)
        barChart.data = barData

        // Customize the bar chart
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.isEnabled = false

        // Customize the X-Axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.labelCount = waterIntakeData.size
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "Day ${(value + 1).toInt()}" // Display days as labels
            }
        }

        // Customize animation
        barChart.animateY(1000)

        // Refresh the chart
        barChart.invalidate()
    }
}