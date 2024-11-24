package com.marshielo.seeheart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Ganti activity setelah beberapa detik, misalnya 2 detik
        val thread = Thread {
            Thread.sleep(2000)
            val intent = Intent(this, Onboarding1Activity::class.java)
            startActivity(intent)
            finish()
        }
        thread.start()
    }
}

