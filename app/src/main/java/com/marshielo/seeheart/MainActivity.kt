package com.marshielo.seeheart

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.widget.TextViewCompat
import com.marshielo.seeheart.ui.theme.SeeHeartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the custom Typeface from assets/fonts

        setContentView(R.layout.main_activity)
        val typeface = Typeface.createFromAsset(assets, "fonts/Inter-VariableFont_opsz,wght.ttf")

        // Apply the custom font globally to all TextViews
        applyCustomFontToViews(typeface, window.decorView as ViewGroup)


        Handler(Looper.getMainLooper()).postDelayed(
            {
                val i = Intent(this, CardActivity::class.java)
                startActivity(i)
                finish()
            }, 3000
        )

    }

    private fun applyCustomFontToViews(typeface: Typeface, viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView) {
                child.typeface = typeface
            } else if (child is ViewGroup) {
                applyCustomFontToViews(typeface, child)
            }
        }
    }

}

