package com.marshielo.seeheart

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
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

        // Inisialisasi RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReminder)

        // Inisialisasi adapter dengan callback
        reminderAdapter = ReminderAdapter(reminderList, { reminder ->
            // Callback untuk menandai reminder selesai
            Toast.makeText(this, "${reminder.description} completed", Toast.LENGTH_SHORT).show()
        }, {
            // Callback untuk menyimpan reminders
            saveReminders()
        })

        recyclerView.adapter = reminderAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)



        // Load data dari SharedPreferences
        loadReminders()

        // Tombol Clear Completed
        findViewById<Button>(R.id.btnClearCompleted).setOnClickListener {
            // Hapus semua reminder yang sudah selesai
            reminderList.removeAll { it.isCompleted }

            // Perbarui RecyclerView
            reminderAdapter.notifyDataSetChanged()

            // Simpan perubahan ke SharedPreferences
            saveReminders()
        }

        // Floating Action Button untuk menambah reminder
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
        val iconRes = intent.getIntExtra("iconRes", R.drawable.ic_add) // Default to ic_add if not found

        // Debugging: Print received values
        println("Received Task Title: $taskTitle")
        println("Received Date: $date")
        println("Received Time: $time")
        println("Received Icon Resource: $iconRes")

        // Only add if data is valid and not empty
        if (!taskTitle.isNullOrEmpty() && !date.isNullOrEmpty() && !time.isNullOrEmpty()) {
            val newReminder = ReminderItem(iconRes, "$taskTitle\n$date\n$time")

            // Check if reminder already exists to avoid duplicates
            if (!reminderList.contains(newReminder)) {
                reminderList.add(newReminder)
                reminderAdapter.notifyDataSetChanged()
                saveReminders() // Save reminders to SharedPreferences after adding new one
            } else {
                Toast.makeText(this, "Reminder already exists!", Toast.LENGTH_SHORT).show()
            }

            // Clear the intent data after use
            intent.removeExtra("taskTitle")
            intent.removeExtra("date")
            intent.removeExtra("time")
            intent.removeExtra("iconRes")
        }
    }

    // Fungsi untuk memuat data dari SharedPreferences
    private fun loadReminders() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Reminders", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reminderList", null)

        if (json != null) {
            val type = object : TypeToken<List<ReminderItem>>() {}.type
            val savedReminders: List<ReminderItem> = gson.fromJson(json, type)
            reminderList.clear() // Bersihkan list sebelumnya
            reminderList.addAll(savedReminders) // Tambahkan list yang disimpan
        }

        reminderAdapter.notifyDataSetChanged()
    }

    // Fungsi untuk menyimpan data ke SharedPreferences
    private fun saveReminders() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Reminders", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(reminderList)
        editor.putString("reminderList", json)
        editor.apply()
    }
}
