package com.marshielo.seeheart

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class AddReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        val taskTitleRadioGroup = findViewById<RadioGroup>(R.id.rgTaskTitle)
        val reminderDate = findViewById<EditText>(R.id.etReminderDate)
        val reminderTime = findViewById<EditText>(R.id.etReminderTime)
        val repeatSwitch = findViewById<Switch>(R.id.switchRepeat)
        val saveButton = findViewById<Button>(R.id.btnSaveReminder)

        // Date Picker
        reminderDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    reminderDate.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Time Picker
        reminderTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    reminderTime.setText("$hourOfDay:$minute")
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // Save Button Logic
        saveButton.setOnClickListener {
            val selectedTaskTitleId = taskTitleRadioGroup.checkedRadioButtonId
            val selectedTaskTitle = findViewById<RadioButton>(selectedTaskTitleId).text.toString()
            val date = reminderDate.text.toString()
            val time = reminderTime.text.toString()
            val isRepeat = repeatSwitch.isChecked

            if (selectedTaskTitle.isEmpty() || date.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Determine icon based on selected task
                val iconRes = when (selectedTaskTitle) {
                    "Bangun" -> R.drawable.reminderwake
                    "Minum" -> R.drawable.reminderwater
                    "Beraktivitas" -> R.drawable.reminderwalk
                    "Tidur" -> R.drawable.remindersleep
                    else -> R.drawable.ic_add // Default icon if none matches
                }

                // Pass data back to ReminderActivity with the selected icon
                val intent = Intent(this, ReminderActivity::class.java)
                intent.putExtra("taskTitle", selectedTaskTitle)
                intent.putExtra("date", date)
                intent.putExtra("time", time)
                intent.putExtra("isRepeat", isRepeat)
                intent.putExtra("iconRes", iconRes)  // Pass the selected icon resource
                startActivity(intent)
                finish()
            }
        }
    }
}

