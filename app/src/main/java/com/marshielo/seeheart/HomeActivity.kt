package com.marshielo.seeheart
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.marshielo.seeheart.data.database.AppDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.marshielo.seeheart.R
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize database
        database = AppDatabase.getDatabase(this)

        // Load weekly data and display it on the LineChart
        loadWeeklyData()
    }

    private fun loadWeeklyData() {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Prepare list of dates for the last 7 days
        val last7Days = mutableListOf<String>()
        for (i in 0..6) {
            last7Days.add(sdf.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }

        lifecycleScope.launch {
            val weeklyData = database.waterDao().getWeeklyIntake()
            val weeklyIntake = weeklyData.associate { it.date to it.totalIntake }

            setupLineChart(weeklyIntake)
        }

    }

    private fun setupLineChart(weeklyIntake: Map<String, Int>) {
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val entries = ArrayList<Entry>()

        // Convert data ke format Entry
        var index = 0
        for ((_, intake) in weeklyIntake.toSortedMap()) {
            entries.add(Entry(index.toFloat(), intake.toFloat()))
            index++
        }

        // Tambahkan entri default jika tidak ada data
        if (entries.isEmpty()) {
            entries.add(Entry(0f, 0f)) // Default entry jika data kosong
        }

        val lineDataSet = LineDataSet(entries, "Water Intake (ml)")
        lineDataSet.color = ContextCompat.getColor(this, R.color.teal_200)
        lineDataSet.valueTextSize = 12f
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.teal_700))

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Kustomisasi grafik
        lineChart.description.text = "7-Day Water Intake"
        lineChart.setPinchZoom(true)
        lineChart.animateX(1000)
        lineChart.invalidate()
    }

}
