package com.example.gokulahealth

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val dashboardText =
            findViewById<TextView>(R.id.dashboardText)

        val cowName =
            intent.getStringExtra("cowName")

        val avgMilk =
            intent.getFloatExtra("avgMilk", 0f)

        val vaccine =
            intent.getStringExtra("vaccine")

        dashboardText.text =
            "Cow Name: $cowName\n\n" +
                    "Monthly Average Yield: $avgMilk L\n\n" +
                    "Vaccination Date: $vaccine"
    }
}