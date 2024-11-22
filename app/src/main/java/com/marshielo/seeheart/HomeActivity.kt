package com.marshielo.seeheart

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Anda bisa tambahkan fungsi lain sesuai kebutuhan

        // Set active state for CardActivity
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navReminder = findViewById<ImageView>(R.id.navReminder)
        val navNotes = findViewById<ImageView>(R.id.navNotes)



    }
}
