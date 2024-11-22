package com.marshielo.seeheart

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ReminderActivity : AppCompatActivity() {

    private lateinit var reminderAdapter: ReminderAdapter
    private val reminderList = mutableListOf<ReminderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReminder)
        reminderAdapter = ReminderAdapter(reminderList) { reminder ->
            // Mark reminder as complete
            reminderList.remove(reminder)
            reminderAdapter.notifyDataSetChanged()
            saveReminders()
        }
        recyclerView.adapter = reminderAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load initial data
        loadReminders()

        // Add new reminder
        findViewById<FloatingActionButton>(R.id.fabAddReminder).setOnClickListener {
            val intent = Intent(this, AddReminderActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Check if the intent has extra data (for new reminder)
        val taskTitle = intent.getStringExtra("taskTitle")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        val isRepeat = intent.getBooleanExtra("isRepeat", false)

        // Only add if data is valid and not empty
        if (!taskTitle.isNullOrEmpty() && !date.isNullOrEmpty() && !time.isNullOrEmpty()) {
            val newReminder = ReminderItem(R.drawable.ic_add, "$taskTitle\n$date\n$time")

            // Check if reminder already exists to avoid duplicates
            if (!reminderList.contains(newReminder)) {
                reminderList.add(newReminder)
                reminderAdapter.notifyDataSetChanged()
                saveReminders()  // Save reminders to SharedPreferences after adding new one
            } else {
                Toast.makeText(this, "Reminder already exists!", Toast.LENGTH_SHORT).show()
            }

            // Clear the intent data after use
            intent.removeExtra("taskTitle")
            intent.removeExtra("date")
            intent.removeExtra("time")
            intent.removeExtra("isRepeat")
        }
    }

    private fun loadReminders() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Reminders", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reminderList", null)

        if (json != null) {
            val type = object : TypeToken<List<ReminderItem>>() {}.type
            val savedReminders: List<ReminderItem> = gson.fromJson(json, type)
            reminderList.addAll(savedReminders)
        }

        reminderAdapter.notifyDataSetChanged()
    }

    private fun saveReminders() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Reminders", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(reminderList)
        editor.putString("reminderList", json)
        editor.apply()
    }
}
