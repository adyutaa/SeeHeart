package com.marshielo.seeheart

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.marshielo.seeheart.data.database.AppDatabase
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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

        // Initialize ImageViews
        val tipsImage = findViewById<ImageView>(R.id.tipsImage)
        val tipsImage2 = findViewById<ImageView>(R.id.tipsImage2)
        val navNotes = findViewById<ImageView>(R.id.navNotes)
        val navReminder = findViewById<ImageView>(R.id.navReminder)

        // Set onClickListener for TipsImage
        tipsImage.setOnClickListener {
            val url = "https://www.beautynesia.id/wellness/sering-lupa-minum-air-putih-ini-5-cara-agar-kamu-tetap-terhidrasi-sepanjang-hari/b-283718" // Ganti dengan URL artikel 1
            openBrowser(url)
        }

        // Set onClickListener for TipsImage2
        tipsImage2.setOnClickListener {
            val url = "https://news.schoolmedia.id/artikel/6-Cara-Mengatasi-Kebiasaan-Malas-Minum-Air-Putih-306" // Ganti dengan URL artikel 2
            openBrowser(url)
        }

        // Set onClickListener for navNotes
        navNotes.setOnClickListener {
            val intent = Intent(this, CardActivity::class.java)
            startActivity(intent)
        }

        // Set onClickListener for navReminder
        navReminder.setOnClickListener {
            val intent = Intent(this, ReminderActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to open browser
    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun loadWeeklyData() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault()) // For weekday names

        lifecycleScope.launch {
            try {
                // Fetch weekly intake data
                val weeklyData = database.waterDao().getWeeklyIntake()
                val weekDates = getWeekDates()

                // Map data to the week range
                val weeklyIntakeMap = weekDates.associateWith { date ->
                    weeklyData.find { it.date == date }?.totalIntake ?: 0
                }

                // Generate day labels
                val dayLabels = weekDates.map { date ->
                    sdf.parse(date)?.let { dayFormatter.format(it) } ?: "N/A"
                }

                setupLineChart(weeklyIntakeMap, dayLabels)

            } catch (e: Exception) {
                Log.e("HomeActivity", "Error loading data: ${e.message}")
            }
        }
    }

    private fun getWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek) // Start from Sunday
        return (0..6).map {
            val date = calendar.time
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        }
    }

    private fun setupLineChart(weeklyIntake: Map<String, Int>, dayLabels: List<String>) {
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val entries = ArrayList<Entry>()

        // Prepare data entries for the chart
        weeklyIntake.values.forEachIndexed { index, intake ->
            entries.add(Entry(index.toFloat(), intake.toFloat()))
        }

        // Create the dataset for the chart
        val lineDataSet = LineDataSet(entries, "Water Intake")
        lineDataSet.color = ContextCompat.getColor(this, R.color.blue)
        lineDataSet.valueTextSize = 10f
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.blue))
        lineDataSet.setDrawCircles(true)
        lineDataSet.circleRadius = 4f
        lineDataSet.setDrawValues(false)
        lineDataSet.lineWidth = 3f
        lineDataSet.setDrawFilled(true)
        val drawable = ContextCompat.getDrawable(this, R.drawable.line_chart_fill)
        lineDataSet.fillDrawable = drawable
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize the X-Axis to display weekday labels
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels) // Set weekday names as labels
        xAxis.granularity = 1f
        xAxis.textColor = ContextCompat.getColor(this, R.color.gray)
        xAxis.textSize = 12f
        xAxis.setDrawGridLines(false)

        // Customize other chart elements
        lineChart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisLeft.apply {
                textColor = ContextCompat.getColor(context, R.color.gray)
                textSize = 12f
                setDrawGridLines(true)
                gridColor = ContextCompat.getColor(context, R.color.light_gray)
                axisLineColor = ContextCompat.getColor(context, R.color.transparent)
            }
            axisRight.isEnabled = false
            setPinchZoom(false)
            animateX(1000)
        }

        lineChart.invalidate() // Refresh the chart
    }
}
