package com.marshielo.seeheart

import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.marshielo.seeheart.data.database.AppDatabase
import com.marshielo.seeheart.data.database.SleepDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SleepActivity : AppCompatActivity() {

    private lateinit var sleepLogAdapter: SleepLogAdapter
    private lateinit var sleepRecyclerView: RecyclerView
    private lateinit var manageSubscriptionButton: Button
    private lateinit var sleepTitle: TextView
    private lateinit var sleepProgressBar: CircularProgressBar
    private lateinit var sleepHourTextView: TextView
    private lateinit var sleepLineChart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        // Initialize views
        sleepRecyclerView = findViewById(R.id.sleep_logs_recyclerview)
        manageSubscriptionButton = findViewById(R.id.subscribe_sleep_button)
        sleepTitle = findViewById(R.id.sleep_title)
        sleepProgressBar = findViewById(R.id.sleepProgress)
        sleepHourTextView = findViewById(R.id.sleepHour)
        sleepLineChart = findViewById(R.id.sleepLineChart)

        // Set up RecyclerView
        sleepLogAdapter = SleepLogAdapter()
        sleepRecyclerView.layoutManager = LinearLayoutManager(this)
        sleepRecyclerView.adapter = sleepLogAdapter

        // Load sleep data
        loadSleepData()

        // Set up button click listener for managing subscription
        manageSubscriptionButton.setOnClickListener {
            val intent = Intent(this, SleepSubscriptionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadSleepData() {
        val db = AppDatabase.getDatabase(this)
        val sleepDataDao = db.sleepDataDao()

        lifecycleScope.launch {
            val sleepDataList = withContext(Dispatchers.IO) {
                sleepDataDao.getAllSleepData() // Fetch all sleep data from database
            }

            val weekDates = getWeekDates()
            val weekLabels = getShortWeekLabels()

            // Map data to the week range
            val weeklySleepMap = weekDates.associateWith { date ->
                sleepDataList.filter { sleepData ->
                    val startDate = sleepData.startTime.toLocalDate()
                    startDate.toString() == date
                }.sumOf { it.durationInHours }
            }

            // Prepare Bar Entries
            val barEntries = weeklySleepMap.values.mapIndexed { index, sleepHours ->
                BarEntry(index.toFloat(), sleepHours.toFloat())
            }

            // Render the chart using short labels
            renderLineChart(barEntries, weekLabels)

            // Calculate total sleep hours for today
            val totalSleepHoursToday = calculateTodaySleepHours(sleepDataList)
            updateProgressBar(totalSleepHoursToday)
        }
    }

    // Function to get ISO formatted dates for the week
    private fun getWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek) // Start from Sunday
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return (0..6).map {
            val date = formatter.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            date
        }
    }

    // Function to get shortened day names for the week
    private fun getShortWeekLabels(): List<String> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek) // Start from Sunday
        val formatter = java.text.SimpleDateFormat("EEE", Locale.getDefault()) // Shortened day names
        return (0..6).map {
            val label = formatter.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            label
        }
    }

    private fun renderLineChart(lineEntries: List<Entry>, days: List<String>) {
        // Create the LineDataSet
        val lineDataSet = LineDataSet(lineEntries, "Weekly Sleep Hours")
        lineDataSet.color = Color.parseColor("#5B75A3")  // Line color
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.valueTextSize = 12f
        lineDataSet.setDrawCircles(true) // Show points on the line
        lineDataSet.setCircleColor(Color.BLACK) // Point color
        lineDataSet.circleRadius = 6f
        lineDataSet.setDrawValues(true) // Show values on top of points
        lineDataSet.lineWidth = 2f

        // Dashed line effect
        lineDataSet.enableDashedLine(10f, 5f, 0f)

        // Gradient fill
        lineDataSet.setDrawFilled(true)
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#90A9E0"),
                Color.parseColor("#80FFFFFF")
            )
        )
        lineDataSet.fillDrawable = gradientDrawable

        // Set data and refresh chart
        val lineData = LineData(lineDataSet)
        sleepLineChart.data = lineData

        // Customize X-axis
        sleepLineChart.xAxis.apply {
            granularity = 1f
            valueFormatter = IndexAxisValueFormatter(days) // Days as labels
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textSize = 12f
            textColor = ContextCompat.getColor(this@SleepActivity, R.color.gray)
        }

        // Customize chart appearance
        sleepLineChart.apply {
            description.isEnabled = false // Disable description text
            legend.isEnabled = false // Disable legend
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
                textColor = ContextCompat.getColor(context, R.color.gray)
                textSize = 12f
            }
            axisRight.isEnabled = false // Disable right axis
            setPinchZoom(false)
            animateX(1000) // Animate X-axis
        }

        sleepLineChart.invalidate() // Refresh the chart
    }

    private fun calculateTodaySleepHours(sleepDataList: List<SleepDataEntity>): Double {
        val today = LocalDate.now()
        return sleepDataList.filter {
            val sleepStart = it.startTime.toLocalDate()
            sleepStart == today
        }.sumOf { it.durationInHours }
    }

    private fun updateProgressBar(totalSleepHours: Double) {
        val targetSleepHours = 8.0
        val progress = ((totalSleepHours / targetSleepHours) * 100).toFloat()

        sleepProgressBar.setProgressWithAnimation(progress, 1000)
        sleepHourTextView.text = "%.1f / %.0f hours".format(totalSleepHours, targetSleepHours)
    }
}
