package com.marshielo.seeheart

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        }
        recyclerView.adapter = reminderAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load initial data
        loadReminders()

//        // Add new reminder
//        findViewById<FloatingActionButton>(R.id.fabAddReminder).setOnClickListener {
//            val intent = Intent(this, AddReminderActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun loadReminders() {
        reminderList.add(ReminderItem(R.drawable.reminderwake, "Bangun Pagi"))
        reminderList.add(ReminderItem(R.drawable.reminderwater, "Minum Air"))
        reminderList.add(ReminderItem(R.drawable.reminderwalk, "Jogging"))
        reminderList.add(ReminderItem(R.drawable.remindersleep, "Tidur"))
        reminderAdapter.notifyDataSetChanged()
    }
}
