package com.marshielo.seeheart

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

class BMIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)

        val height = findViewById<EditText>(R.id.eTHeight)
        val weight = findViewById<EditText>(R.id.eTWeight)
        val btnBMI = findViewById<Button>(R.id.btnBMI)
        val result = findViewById<TextView>(R.id.tVHasil)

        btnBMI.setOnClickListener{
            val h= (height.text.toString()).toFloat()/100
            val w= (weight.text.toString()).toFloat()

            val r= w/(h*h)
            result.text = "BMI anda adalah $r"
        }

    }
}